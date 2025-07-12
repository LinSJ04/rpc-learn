package com.work.rpc.client.utils;

import com.work.rpc.proxy.RpcClientProxy;
import com.work.rpc.transmission.RpcClient;
import com.work.rpc.transmission.socket.client.SocketRpcClient;

public class ProxyUtils {
    private static final RpcClient rpcClient = new SocketRpcClient("localhost", 8888);
    private static final RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);

    public static <T> T getProxy(Class<T> clazz) {
        return rpcClientProxy.getProxy(clazz);
    }
}
