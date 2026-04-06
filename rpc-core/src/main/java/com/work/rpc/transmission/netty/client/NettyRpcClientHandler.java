package com.work.rpc.transmission.netty.client;

import com.work.rpc.constant.RpcConstant;
import com.work.rpc.dto.RpcMsg;
import com.work.rpc.dto.RpcResp;
import com.work.rpc.enums.CompressType;
import com.work.rpc.enums.MsgType;
import com.work.rpc.enums.SerializeType;
import com.work.rpc.enums.VersionType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcMsg> {

    // 继承SimpleChannelInboundHandler，不需要手动release连接 这里不需要手动release的是什么？
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg rpcMsg) throws Exception {
        if (rpcMsg.getMsgType() == MsgType.HEARTBEAT_RESP) {
            log.debug("收到服务端心跳响应, {}", rpcMsg);
            return;
        }
        // 收到请求响应
        // 客户端接收到服务端数据
        log.debug("收到服务端数据： {}", rpcMsg);
        RpcResp<?> rpcResp = (RpcResp<?>) rpcMsg.getData();
        UnprocessedRpcReq.complete(rpcResp);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 5s没写会触发这个userEvent
        boolean isNeedHearBeat = evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE;

        if (!isNeedHearBeat) {
            super.userEventTriggered(ctx, evt);
            return;
        }
        RpcMsg msg = RpcMsg.builder()
                .version(VersionType.VERSION1)
                .msgType(MsgType.HEARTBEAT_REQ)
                .serializeType(SerializeType.KRYO)
                .compressType(CompressType.GZIP)
                .build();

        log.info("客户端发送心跳, {}", msg);
        // 失败关闭channel
        ctx.writeAndFlush(msg)
            .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 发生异常做一些事情
        log.error("服务端发生异常", cause);
        // 关闭连接
        ctx.close();
    }
}
