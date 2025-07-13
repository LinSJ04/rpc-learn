package com.work.rpc.registry.impl;

import cn.hutool.core.util.StrUtil;
import com.work.rpc.constant.RpcConstant;
import com.work.rpc.factory.SingletonFactory;
import com.work.rpc.registry.ServiceRegistry;
import com.work.rpc.registry.zk.ZkClient;
import com.work.rpc.util.IPUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;

@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {
    private final ZkClient zkClient;

    public ZkServiceRegistry() {
        this(SingletonFactory.getInstance(ZkClient.class));
    }

    public ZkServiceRegistry(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress address) {
        log.info("服务注册, rpcServiceName: {}, address: {}", rpcServiceName, address);

        String path = RpcConstant.ZK_RPC_ROOT_PATH
                + StrUtil.SLASH
                + rpcServiceName
                + StrUtil.SLASH
                + IPUtils.tpIpPort(address);

        zkClient.createPersistentNode(path);
    }

    @SneakyThrows
    @Override
    public void clearAll() {
        // 注册的时候用的是hostname，所以这里也用hostname
        // todo 其实有可以改进的地方
        String host = InetAddress.getLocalHost().getHostName();
        int port = RpcConstant.SERVER_PORT;
        zkClient.clearAll(new InetSocketAddress(
                host, port));
    }
}
