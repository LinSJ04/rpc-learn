package com.work.rpc.loadbalance.impl;

import cn.hutool.core.util.RandomUtil;
import com.work.rpc.dto.RpcReq;
import com.work.rpc.loadbalance.LoadBalance;

import java.util.List;

public class RandomLoadBalance implements LoadBalance {

    @Override
    public String select(List<String> list, RpcReq rpcReq) {
        return RandomUtil.randomEle(list);
    }
}
