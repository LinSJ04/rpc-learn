package com.work.rpc.client.utils;

import com.work.rpc.proxy.RpcClientProxy;
import com.work.rpc.transmission.RpcClient;
import com.work.rpc.transmission.socket.client.SocketRpcClient;

public class ProxyUtils {
    // 指定了ip和端口号
    // 但是实际应用中，可能会有多个服务器提供服务
    // 并且地址可能会有变化
    // 服务器的个数也会变化
    // 需要有一套发现机制，来发现服务器的地址
    // 提供服务的服务器需要注册服务，此处的服务和现在的Provider不同，指的是整个应用服务
    private static final RpcClient rpcClient = new SocketRpcClient("localhost", 8888);
    private static final RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);

    public static <T> T getProxy(Class<T> clazz) {
        return rpcClientProxy.getProxy(clazz);
    }
}
