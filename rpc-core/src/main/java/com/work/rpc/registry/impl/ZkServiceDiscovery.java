package com.work.rpc.registry.impl;

import cn.hutool.core.util.StrUtil;
import com.work.rpc.constant.RpcConstant;
import com.work.rpc.dto.RpcReq;
import com.work.rpc.factory.SingletonFactory;
import com.work.rpc.loadbalance.LoadBalance;
import com.work.rpc.loadbalance.RandomLoadBalance;
import com.work.rpc.registry.ServiceDiscovery;
import com.work.rpc.registry.zk.ZkClient;
import com.work.rpc.util.IPUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    private final ZkClient zkClient;
    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        this(SingletonFactory.getInstance(ZkClient.class),
                SingletonFactory.getInstance(RandomLoadBalance.class));
    }

    public ZkServiceDiscovery(ZkClient zkClient, LoadBalance loadBalance) {
        this.zkClient = zkClient;
        this.loadBalance = loadBalance;
    }

    @Override
    public InetSocketAddress lookupService(RpcReq rpcReq) {
        String serviceName = rpcReq.rpcServiceName();
        String path = RpcConstant.ZK_RPC_ROOT_PATH
                + StrUtil.SLASH
                + serviceName;
        List<String> children = zkClient.getChildrenNodes(path);
        String address = loadBalance.select(children);
        return IPUtils.toInetSocketAddress(address);
    }
}
