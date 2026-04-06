package com.work.rpc.transmission.netty.client;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ChannelPool {
    private final Map<String, Channel> pool = new ConcurrentHashMap<>();

    public Channel get(InetSocketAddress addr, Supplier<Channel> supplier) {
        //
        String addrString = addr.toString();
        Channel channel = pool.get(addrString);
        if (channel != null && channel.isActive()) {
            return channel;
        }

        // 从supplier创建新的channel
        // 如果没有，创建一个新channel
        Channel newChannel = supplier.get();
        pool.put(addrString, newChannel);
        return newChannel;
    }

}
