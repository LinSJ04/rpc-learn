package com.work.rpc.transmission;

import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;

public interface RpcClient {
    RpcResp<?> sendReq(RpcReq req);
}
