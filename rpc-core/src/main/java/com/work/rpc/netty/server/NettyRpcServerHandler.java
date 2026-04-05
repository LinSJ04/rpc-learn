package com.work.rpc.netty.server;

import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String str) throws Exception {
        log.debug("接收到客户端请求: {}", str);
//        RpcResp<String> rpcResp = RpcResp.success(str, "模拟响应数据");
        // 成功之后关闭
        ctx.channel()
            .writeAndFlush("模拟响应数据")
            .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端异常", cause);
        ctx.close();
    }
}
