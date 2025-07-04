package com.work.rpc.handler;

import com.work.rpc.dto.RpcReq;
import com.work.rpc.provider.ServiceProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcReqHandler { // 专门用于处理请求
    private final ServiceProvider serviceProvider;

    public RpcReqHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @SneakyThrows // Lombok, 相当于帮忙做了try catch
    public Object invoke(RpcReq rpcReq) {
        String rpcServiceName = rpcReq.rpcServiceName();
        Object service = serviceProvider.getService(rpcServiceName);

        // 获取到全类名
        log.debug("获取到对应服务：{}", service.getClass().getCanonicalName());
        // 使用反射获取Class对象
        return service.getClass()
                .getMethod(rpcReq.getMethodName(), rpcReq.getParameterTypes())
                .invoke(service, rpcReq.getParameters());
    }
}
