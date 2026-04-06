package com.work.rpc.transmission;

import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;

import java.util.concurrent.Future;

public interface RpcClient {
    // 将异常交给上层来控制
    Future<RpcResp<?>> sendReq(RpcReq req);
}
