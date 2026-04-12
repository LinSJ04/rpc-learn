package com.work.rpc.loadbalance;

import com.work.rpc.dto.RpcReq;

import java.util.List;

public interface LoadBalance {
    String select(List<String> list, RpcReq rpcReq);
}
