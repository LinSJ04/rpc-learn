package com.work.rpc.transmission.netty.server;

import com.work.rpc.dto.RpcMsg;
import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;
import com.work.rpc.enums.CompressType;
import com.work.rpc.enums.MsgType;
import com.work.rpc.enums.SerializeType;
import com.work.rpc.enums.VersionType;
import com.work.rpc.handler.RpcReqHandler;
import com.work.rpc.provider.ServiceProvider;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcMsg> {
    private final RpcReqHandler rpcReqHandler;

    public NettyRpcServerHandler(ServiceProvider serviceProvider) {
        this.rpcReqHandler = new RpcReqHandler(serviceProvider);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg rpcMsg) throws Exception {
        log.debug("接收到客户端请求: {}", rpcMsg);
        // rpcReq是业务的请求id
        // RpcMsg的reqId是协议的请求id

        MsgType msgType;
        Object data;

        // 区分心跳请求/rpc请求
        if (rpcMsg.getMsgType().isHeartbeat()) {
            msgType = MsgType.HEARTBEAT_RESP;
            data = null;
        } else {
            msgType = MsgType.RPC_RESP;
            // 调用方法处理请求
            RpcReq rpcReq = (RpcReq) rpcMsg.getData();
            data = handleRpcReq(rpcReq);
        }

        RpcMsg msg = RpcMsg.builder()
                        .reqId(rpcMsg.getReqId())
                        .version(VersionType.VERSION1)
                        .msgType(msgType)
                        .serializeType(SerializeType.KRYO)
                        .compressType(CompressType.GZIP)
                        .data(data)
                        .build();
        // 成功之后关闭
        ctx.channel()
            .writeAndFlush(msg)
            .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端异常", cause);
        ctx.close();
    }

    private RpcResp<?> handleRpcReq(RpcReq rpcReq) {
        try {
            Object object = rpcReqHandler.invoke(rpcReq);
            return RpcResp.success(rpcReq.getReqId(), object);
        } catch (Exception e) {
            log.info("调用失败", e);
            return RpcResp.fail(rpcReq.getReqId(), e.getMessage());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 5s没写会触发这个userEvent
        boolean isNeedClose = evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state() == IdleState.READER_IDLE;

        if (!isNeedClose) {
            super.userEventTriggered(ctx, evt);
            return;
        }
        log.debug("服务端长时间没有收到客户端的心跳，关闭channel，addr: {}", ctx.channel().remoteAddress());
        ctx.channel().close();
    }
}
