package com.work.rpc.proxy;

import cn.hutool.core.util.IdUtil;
import com.work.rpc.config.RpcServiceConfig;
import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;
import com.work.rpc.enums.RpcRespStatus;
import com.work.rpc.exception.RpcException;
import com.work.rpc.transmission.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

public class RpcClientProxy implements InvocationHandler { // jdk动态代理，需要实现InvocationHandler接口
    private final RpcClient rpcClient;
    private final RpcServiceConfig config;

    public RpcClientProxy(RpcClient rpcClient) {
        this(rpcClient, new RpcServiceConfig()); // RpcServiceConfig的service在客户端不用传
    }

    public RpcClientProxy(RpcClient rpcClient, RpcServiceConfig config) {
        this.rpcClient = rpcClient;
        this.config = config;
    }

    /**
     * 获取代理对象
     * @return
     * @param <T> 代理对象类型
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) { // 传接口，invoke接口调用方法之前/之后都会做一些事情
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(), // 被代理对象的类的类加载
                new Class[]{clazz}, // 被代理对象实现的接口
                this); // InvocationHandler 实现类对象 就是当前对象
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 调用对象的方法的时候，就会被拦截到，然后执行invoke方法
        RpcReq req = RpcReq.builder()
                .reqId(IdUtil.fastSimpleUUID()) // 使用 Hutool 的 fastSimpleUUID 生成唯一请求 ID
                .interfaceName(method.getDeclaringClass().getCanonicalName()) // 获取接口全类名
                .methodName(method.getName()) // 获取方法名
                .parameters(args)
                .parameterTypes(method.getParameterTypes())
                .version(config.getVersion()) // 获取版本号 传config的原因，需要根据config找到对应的实现类
                .group(config.getGroup()) // 获取分组
                .build();
        RpcResp<?> rpcResp = rpcClient.sendReq(req); // 发送请求(传入RpcClient的原因)
        check(req, rpcResp); // 检查响应是否正确
        return rpcResp.getData(); // 返回响应数据
    }

    private void check(RpcReq rpcReq, RpcResp<?> rpcResp) {
        if (Objects.isNull(rpcResp)) {
            throw new RpcException("rpcResp为空");
        }
        if (!Objects.equals(rpcResp.getReqId(), rpcReq.getReqId())) {
            throw new RpcException("请求Id与响应Id不一致");
        }
        if (RpcRespStatus.isFail(rpcResp.getCode())) {
            throw new RpcException("响应值为失败：" + rpcResp.getMsg());
        }
    }
}
