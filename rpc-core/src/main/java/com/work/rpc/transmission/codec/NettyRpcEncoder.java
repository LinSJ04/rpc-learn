package com.work.rpc.transmission.codec;

import com.work.rpc.compress.Compress;
import com.work.rpc.compress.impl.GzipCompress;
import com.work.rpc.constant.RpcConstant;
import com.work.rpc.dto.RpcMsg;
import com.work.rpc.enums.MsgType;
import com.work.rpc.factory.SingletonFactory;
import com.work.rpc.serialize.Serializer;
import com.work.rpc.serialize.impl.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.swing.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyRpcEncoder extends MessageToByteEncoder<RpcMsg> {
    // CAS自旋实现线程安全
    private static final AtomicInteger ID_GEN = new AtomicInteger(0);
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMsg rpcMsg, ByteBuf byteBuf) throws Exception {
        // object ==> byte[]
        // byte[] 放到 ByteBuf
        // 魔法数 4字节
        byteBuf.writeBytes(RpcConstant.PRC_MAGIC_CODE);
        // 版本号 1字节
        byteBuf.writeByte(rpcMsg.getVersion().getCode());

        // 总长度 4字节 目前还不知道请求体长度，先移动4位
        // 其实也不用那么麻烦，请求体长度，只取决于数据处理后的byte[]长度，可以事先计算
        byteBuf.writerIndex(4);

        // 消息类型 1字节
        byteBuf.writeByte(rpcMsg.getMsgType().getCode());
        // 序列化类型 1字节
        byteBuf.writeByte(rpcMsg.getSerializeType().getCode());
        // 压缩类型 1字节
        byteBuf.writeByte(rpcMsg.getCompressType().getCode());
        // 请求ID 4字节
        byteBuf.writeInt(ID_GEN.getAndIncrement());

        int msgLen = RpcConstant.REQ_HEAD_LEN;
        // 心跳数据不需要带请求体
        if (!MsgType.isHeartbeat(rpcMsg.getMsgType())
            && !Objects.isNull(rpcMsg.getData())) {
            byte[] bytes = data2Bytes(rpcMsg);
            byteBuf.writeBytes(bytes);
            msgLen += bytes.length;
        }

        // full len 填充到对应的idx
        int curIdx = byteBuf.writerIndex();
        byteBuf.writerIndex(curIdx - msgLen + RpcConstant.REQ_HEAD_LEN + 1);
        byteBuf.writeInt(msgLen);

        // 回到末尾的idx
        byteBuf.writerIndex(curIdx);
    }

    private byte[] data2Bytes(RpcMsg rpcMsg) {
        // todo 获取序列化和数据压缩类型
        // 需要配置spi机制来做
//        SerializeType serializeType = rpcMsg.getSerializeType();
//        CompressType compressType = rpcMsg.getCompressType();

        Serializer serializer = SingletonFactory.getInstance(KryoSerializer.class);
        byte[] data = serializer.serialize(rpcMsg.getData());

        Compress compress = SingletonFactory.getInstance(GzipCompress.class);
        return compress.compress(data);
    }
}
