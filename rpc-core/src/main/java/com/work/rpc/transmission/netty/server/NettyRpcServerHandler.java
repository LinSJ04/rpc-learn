package com.work.rpc.transmission.netty.server;

import com.work.rpc.dto.RpcMsg;
import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;
import com.work.rpc.enums.CompressType;
import com.work.rpc.enums.MsgType;
import com.work.rpc.enums.SerializeType;
import com.work.rpc.enums.VersionType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMsg rpcMsg) throws Exception {
        log.debug("接收到客户端请求: {}", rpcMsg);
        RpcReq rpcReq = (RpcReq) rpcMsg.getData();
        // rpcReq是业务的请求id
        // RpcMsg的reqId是协议的请求id
        RpcResp<String> rpcResp = RpcResp.success(rpcReq.getReqId(), "模拟响应数据");

        RpcMsg msg = RpcMsg.builder()
                        .reqId(rpcMsg.getReqId())
                        .version(VersionType.VERSION1)
                        .msgType(MsgType.RPC_RESP)
                        .serializeType(SerializeType.KRYO)
                        .compressType(CompressType.GZIP)
                        .data(rpcResp)
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
}
