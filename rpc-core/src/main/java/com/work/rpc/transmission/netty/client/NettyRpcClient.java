package com.work.rpc.transmission.netty.client;

import com.work.rpc.constant.RpcConstant;
import com.work.rpc.dto.RpcMsg;
import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;
import com.work.rpc.enums.CompressType;
import com.work.rpc.enums.MsgType;
import com.work.rpc.enums.SerializeType;
import com.work.rpc.enums.VersionType;
import com.work.rpc.factory.SingletonFactory;
import com.work.rpc.registry.ServiceDiscovery;
import com.work.rpc.registry.impl.ZkServiceDiscovery;
import com.work.rpc.transmission.RpcClient;
import com.work.rpc.transmission.codec.NettyRpcDecoder;
import com.work.rpc.transmission.codec.NettyRpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NettyRpcClient implements RpcClient {
    private final ServiceDiscovery serviceDiscovery;
    private static final Bootstrap bootstrap;

    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;



    public NettyRpcClient() {
        this(SingletonFactory.getInstance(ZkServiceDiscovery.class));
    }

    public NettyRpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    static {
        bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, DEFAULT_CONNECT_TIMEOUT)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        // 状态监控，客户端5s没有给服务端发数据，就会触发userevent
                        channel.pipeline().addLast(new IdleStateHandler(0, 5, 0));
                        channel.pipeline().addLast(new NettyRpcDecoder());
                        channel.pipeline().addLast(new NettyRpcEncoder());
                        channel.pipeline().addLast(new NettyRpcClientHandler());
                    }
                });
    }
    @SneakyThrows
    @Override
    public RpcResp<?> sendReq(RpcReq req) {
        // 获取对应方法的address
        InetSocketAddress address = serviceDiscovery.lookupService(req);
        // 连接之后会等待
        ChannelFuture channelFuture = bootstrap.connect(address).sync();

        log.info("netty rpc client连接到: {}", address);
        Channel channel = channelFuture.channel();
        // 如果发送，关闭channel

        RpcMsg rpcMsg = RpcMsg.builder()
                .version(VersionType.VERSION1)
                .serializeType(SerializeType.KRYO)
                .compressType(CompressType.GZIP)
                .msgType(MsgType.RPC_REQ)
                .data(req)
                .build();

        channel.writeAndFlush(rpcMsg).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

        // 阻塞等待直到关闭
        channel.closeFuture().sync();
        // handler将数据设置到map中，之后再获取响应数据

        // 获取服务端响应的数据
        // channelcontext会绑定一个AttributeKey
        // 不太理解这边AttributeKey是啥
        AttributeKey<RpcResp<?>> key = AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);

        // 根据AttributeKey去AttributeMap中取对应的value
        RpcResp<?> rpcResp = channel.attr(key).get();
        System.out.println("rpcResp = " + rpcResp);

        return rpcResp;
    }
}
