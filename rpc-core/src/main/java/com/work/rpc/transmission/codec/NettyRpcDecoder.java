package com.work.rpc.transmission.codec;

import com.work.rpc.compress.impl.GzipCompress;
import com.work.rpc.constant.RpcConstant;
import com.work.rpc.dto.RpcMsg;
import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;
import com.work.rpc.enums.CompressType;
import com.work.rpc.enums.MsgType;
import com.work.rpc.enums.SerializeType;
import com.work.rpc.enums.VersionType;
import com.work.rpc.exception.RpcException;
import com.work.rpc.factory.SingletonFactory;
import com.work.rpc.serialize.impl.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.Arrays;

public class NettyRpcDecoder extends LengthFieldBasedFrameDecoder {
    // LengthFieldBasedFrameDecoder extends ByteToMessageDecoder
    // LengthFieldBasedFrameDecoder解决TCP的粘包问题
    public NettyRpcDecoder() {
        // initialBytesToStrip：0，因为要获取整个Msg，目标就是从需要的数据开始
        super(RpcConstant.REQ_MAX_LEN, 5, 4, -9, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        return decodeFrame(frame);
    }

    private Object decodeFrame(ByteBuf byteBuf) {
        byte[] magicBytes = new byte[RpcConstant.PRC_MAGIC_CODE.length];
        byteBuf.readBytes(magicBytes);

        if (!Arrays.equals(magicBytes, RpcConstant.PRC_MAGIC_CODE)) {
            throw new RpcException("魔法值异常:" + new String(magicBytes));
        }

        byte versionTypeCode = byteBuf.readByte();
        VersionType version = VersionType.from(versionTypeCode);

        int msgFullLen = byteBuf.readInt();

        byte msgTypeCode = byteBuf.readByte();
        MsgType msgType = MsgType.from(msgTypeCode);

        byte serializeTypeCode = byteBuf.readByte();
        SerializeType serializeType = SerializeType.from(serializeTypeCode);

        byte compressTypeCode = byteBuf.readByte();
        CompressType compressType = CompressType.from(compressTypeCode);

        int reqId = byteBuf.readInt();

        Object data = readData(byteBuf, msgFullLen - RpcConstant.REQ_HEAD_LEN, msgType);

        // 构造RpcMsg
        // 后面为什么要用RpcMsg
        // encoder 会将RpcMsg 转换为 byte[]
        return RpcMsg.builder()
                .reqId(reqId)
                .msgType(msgType)
                .version(version)
                .compressType(compressType)
                .serializeType(serializeType)
                .data(data)
                .build();
    }

    private Object readData(ByteBuf byteBuf, int dataLen, MsgType msgType) {
        if (msgType.isReq()) {
            return readData(byteBuf, dataLen, RpcReq.class);
        } else {
            return readData(byteBuf, dataLen, RpcResp.class);
        }
    }

    private <T> T readData(ByteBuf byteBuf, int dataLen, Class<T> clazz) {
        if (dataLen <= 0) {
            return null;
        }
        byte[] data = new byte[dataLen];
        byteBuf.readBytes(data);

        GzipCompress compress = SingletonFactory.getInstance(GzipCompress.class);
        data = compress.decompress(data);

        KryoSerializer serializer = SingletonFactory.getInstance(KryoSerializer.class);
        return serializer.deserialize(data, clazz);
    }
}
