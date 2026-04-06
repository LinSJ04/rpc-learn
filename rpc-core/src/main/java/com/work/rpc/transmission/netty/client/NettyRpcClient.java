package com.work.rpc.transmission.netty.client;

import com.work.rpc.constant.RpcConstant;
import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;
import com.work.rpc.transmission.RpcClient;
import com.work.rpc.transmission.codec.NettyRpcDecoder;
import com.work.rpc.transmission.codec.NettyRpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcClient implements RpcClient {
    private static final Bootstrap bootstrap;

    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;

    static {
        bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, DEFAULT_CONNECT_TIMEOUT)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new NettyRpcDecoder());
                        channel.pipeline().addLast(new NettyRpcEncoder());
                        channel.pipeline().addLast(new NettyRpcClientHandler());
                    }
                });
    }
    @SneakyThrows
    @Override
    public RpcResp<?> sendReq(RpcReq req) {
        // 连接之后会等待
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", RpcConstant.SERVER_PORT).sync();

        log.info("netty rpc client连接到xxx");
        Channel channel = channelFuture.channel();
        // 如果发送，关闭channel

        // 接口名模拟发送数据
        String interfaceName = req.getInterfaceName();
        channel.writeAndFlush(interfaceName).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

        // 阻塞等待直到关闭
        channel.closeFuture().sync();
        // handler将数据设置到map中，之后再获取响应数据

        // 获取服务端响应的数据
        // channelcontext会绑定一个AttributeKey
        // 不太理解这边AttributeKey是啥
        AttributeKey<String> key = AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);

        // 根据AttributeKey去AttributeMap中取对应的value
        String s = channel.attr(key).get();
        System.out.println("s = " + s);

        return null;
    }
}
