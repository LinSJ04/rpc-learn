package com.work.rpc.netty.client;

import com.work.rpc.constant.RpcConstant;
import com.work.rpc.dto.RpcResp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<String> {

    // 继承SimpleChannelInboundHandler，不需要手动release连接 这里不需要手动release的是什么？
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String rpcResp) throws Exception {
        // 客户端接收到服务端数据
        log.debug("收到服务端数据： {}", rpcResp);
        AttributeKey<String> key = AttributeKey.valueOf(RpcConstant.NETTY_RPC_KEY);
        ctx.channel().attr(key).set(rpcResp);
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 发生异常做一些事情
        log.error("服务端发生异常", cause);
        // 关闭连接
        ctx.close();
    }
}
