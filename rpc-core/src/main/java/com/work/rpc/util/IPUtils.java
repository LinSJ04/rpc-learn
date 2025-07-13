package com.work.rpc.util;

import cn.hutool.core.util.StrUtil;

import java.net.InetSocketAddress;
import java.util.Objects;

public class IPUtils {

    // ip:port
    public static String tpIpPort(InetSocketAddress address) {
        if (Objects.isNull(address)) {
            throw new IllegalArgumentException("address为空");
        }
        String host = address.getHostString();
        if (StrUtil.equals(host, "localhost")) {
            host = "127.0.0.1";
        }
        return host + StrUtil.COLON + address.getPort();
    }

    public static InetSocketAddress toInetSocketAddress(String address) {
        if (StrUtil.isBlank(address)) {
            throw new IllegalArgumentException("address不能为空");
        }
        String[] parts = address.split(StrUtil.COLON);
        if (parts.length != 2) {
            throw new IllegalArgumentException("address格式错误, address: " + address);
        }
        return new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
    }
}
