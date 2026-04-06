package com.work.rpc.transmission.netty.server;

import com.work.rpc.config.RpcServiceConfig;
import com.work.rpc.constant.RpcConstant;
import com.work.rpc.transmission.RpcServer;
import com.work.rpc.transmission.codec.NettyRpcDecoder;
import com.work.rpc.transmission.codec.NettyRpcEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcServer implements RpcServer {
    @Override
    public void start() {
        NioEventLoopGroup bossEventLoopGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerEventLoopGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new NettyRpcDecoder());
                            channel.pipeline().addLast(new NettyRpcEncoder());
                            channel.pipeline().addLast(new NettyRpcServerHandler());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(RpcConstant.SERVER_PORT).sync();
            log.info("netty rpc server已启动，端口: {}", RpcConstant.SERVER_PORT);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("服务端异常", e);
        } finally {
            bossEventLoopGroup.shutdownGracefully();
            workerEventLoopGroup.shutdownGracefully();
        }
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {

    }
}
