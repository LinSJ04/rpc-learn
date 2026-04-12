package com.work.rpc.loadbalance.impl;

import com.work.rpc.dto.RpcReq;
import com.work.rpc.loadbalance.LoadBalance;

import java.util.List;

public class RoundLoadBalance implements LoadBalance {
    private int last = -1;
    @Override
    public String select(List<String> list, RpcReq rpcReq) {
        last++;
        last = last % list.size();
        return list.get(last);
    }
}
