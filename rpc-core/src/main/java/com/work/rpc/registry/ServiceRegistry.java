package com.work.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册
 */
public interface ServiceRegistry {
    // InetSocketAddress 包含了主机名和端口号
    void registerService(String rpcServiceName, InetSocketAddress address);

    void clearAll();
}
