package com.work.rpc.transmission.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyRpcDecoder extends LengthFieldBasedFrameDecoder {
    // LengthFieldBasedFrameDecoder extends ByteToMessageDecoder
    // LengthFieldBasedFrameDecoder解决TCP的粘包问题
    public NettyRpcDecoder() {
        this(0, 0, 0);
    }

    public NettyRpcDecoder(int maxFrameLength, int lengthFieldOffset, int lengthAdjustment) {
        super(maxFrameLength, lengthFieldOffset, lengthAdjustment);
    }
}
