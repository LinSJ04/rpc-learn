package com.work.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy implements InvocationHandler { // jdk动态代理，需要实现InvocationHandler接口
    private Object target;

    public RpcClientProxy(Object target) {
        this.target = target;
    }

    /**
     * 获取代理对象
     * @return
     * @param <T> 代理对象类型
     */
    public <T> T getProxy() {
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(), // 被代理对象的类的类加载
                target.getClass().getInterfaces(), // 被代理对象实现的接口
                this); // InvocationHandler 实现类对象 就是当前对象
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("执行方法之前做一些事情...");
        Object object = method.invoke(target, args);// 调用代理对象的方法
        System.out.println("执行方法之后做一些事情...");
        return object;
    }
}
