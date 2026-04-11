package com.work.rpc.handler;

import com.work.rpc.annotation.Limit;
import com.work.rpc.dto.RpcReq;
import com.work.rpc.exception.RpcException;
import com.work.rpc.provider.ServiceProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.shaded.com.google.common.util.concurrent.RateLimiter;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RpcReqHandler { // 专门用于处理请求
    private final ServiceProvider serviceProvider;
    private static final Map<String, RateLimiter> RATE_LIMITER_MAP = new ConcurrentHashMap<>();

    public RpcReqHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @SneakyThrows // Lombok, 相当于帮忙做了try catch
    public Object invoke(RpcReq rpcReq) {
        String rpcServiceName = rpcReq.rpcServiceName();
        Object service = serviceProvider.getService(rpcServiceName);

        // 获取到全类名
        log.debug("获取到对应服务：{}", service.getClass().getCanonicalName());
        Method method = service.getClass()
                .getMethod(rpcReq.getMethodName(), rpcReq.getParameterTypes());

        Limit limit = method.getAnnotation(Limit.class);
        if (Objects.isNull(limit)) {
            return method.invoke(service, rpcReq.getParameters());
        }

        RateLimiter rateLimiter = RATE_LIMITER_MAP.computeIfAbsent(rpcServiceName,
                key -> RateLimiter.create(limit.permitsPerSecond()));

        if (!rateLimiter.tryAcquire(limit.timeout(), TimeUnit.MILLISECONDS)) {
            throw new RpcException("系统繁忙，请稍后重试");
        }

        // 使用反射获取Class对象
        return method.invoke(service, rpcReq.getParameters());
    }
}
