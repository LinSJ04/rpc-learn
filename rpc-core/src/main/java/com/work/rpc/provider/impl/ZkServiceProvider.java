package com.work.rpc.provider.impl;

import cn.hutool.core.util.StrUtil;
import com.work.rpc.config.RpcServiceConfig;
import com.work.rpc.constant.RpcConstant;
import com.work.rpc.factory.SingletonFactory;
import com.work.rpc.provider.ServiceProvider;
import com.work.rpc.registry.ServiceRegistry;
import com.work.rpc.registry.impl.ZkServiceRegistry;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ZkServiceProvider implements ServiceProvider {
    private final Map<String, Object> SERVICE_CACHE = new HashMap<>();
    private final ServiceRegistry serviceRegistry;

    public ZkServiceProvider() {
        this(SingletonFactory.getInstance(ZkServiceRegistry.class));
    }

    public ZkServiceProvider(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {
        // 原本注册在内存中，现在注册到zk中
        rpcServiceConfig.rpcServiceNames().forEach(rpcServiceName ->
                publishService(rpcServiceName, rpcServiceConfig.getService()));
    }

    @Override
    public Object getService(String rpcServiceName) {
        if (StrUtil.isBlank(rpcServiceName)) {
            throw new IllegalArgumentException("rpcServiceName为空");
        }
        if (!SERVICE_CACHE.containsKey(rpcServiceName)) {
            throw new IllegalArgumentException("rpcServiceName未注册：" + rpcServiceName);
        }
        return SERVICE_CACHE.get(rpcServiceName);
    }

    @SneakyThrows
    private void publishService(String rpcServiceName, Object service) {
        String host = InetAddress.getLocalHost().getHostName();
        int port = RpcConstant.SERVER_PORT;

        InetSocketAddress address = new InetSocketAddress(host, port);
        serviceRegistry.registerService(rpcServiceName, address);

        SERVICE_CACHE.put(rpcServiceName, service);
    }
}
