package com.work.rpc.loadbalance.impl;

import com.work.rpc.dto.RpcReq;
import com.work.rpc.loadbalance.LoadBalance;
import org.apache.curator.shaded.com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ConsistentHashLoadBalance implements LoadBalance {
    @Override
    public String select(List<String> list, RpcReq rpcReq) {
        String key = rpcReq.rpcServiceName();

        long hashCode = Hashing.murmur3_128().hashString(key, StandardCharsets.UTF_8).asLong();
        int index = Hashing.consistentHash(hashCode, list.size());
        return list.get(index);
    }
}
