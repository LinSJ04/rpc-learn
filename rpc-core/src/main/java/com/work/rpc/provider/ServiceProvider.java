package com.work.rpc.provider;

import com.work.rpc.config.RpcServiceConfig;

public interface ServiceProvider {
    void publishService(RpcServiceConfig rpcServiceConfig);

    /**
     * 获取服务
     * @param rpcServiceName 接口全类名+version+group
     * @return 对应接口(包含version,group)实现类
     */
    Object getService(String rpcServiceName);
}
