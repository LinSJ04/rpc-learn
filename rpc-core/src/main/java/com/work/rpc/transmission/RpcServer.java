package com.work.rpc.transmission;

import com.work.rpc.config.RpcServiceConfig;

public interface RpcServer {
    void start();

    void publishService(RpcServiceConfig rpcServiceConfig);
}
