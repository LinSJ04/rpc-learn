package com.work.rpc.registry;

import com.work.rpc.dto.RpcReq;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 * 用于根据请求信息查找服务提供者的地址
 */
public interface ServiceDiscovery {
    InetSocketAddress lookupService(RpcReq rpcReq);
}
