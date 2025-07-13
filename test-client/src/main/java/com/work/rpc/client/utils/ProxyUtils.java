package com.work.rpc.client.utils;

import com.work.rpc.factory.SingletonFactory;
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
    // zk中，把可以提供UserService的服务注册到UserService节点下
    // 服务端把自己的ip和端口以及可以提供的接口注册到zk中
    // 客户端从zk中获取服务端的ip和端口以及可以提供的接口
    private static final RpcClient rpcClient = SingletonFactory.getInstance(SocketRpcClient.class);
    private static final RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);

    public static <T> T getProxy(Class<T> clazz) {
        return rpcClientProxy.getProxy(clazz);
    }
}
