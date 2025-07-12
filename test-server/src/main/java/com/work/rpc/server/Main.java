package com.work.rpc.server;

import com.work.rpc.api.User;
import com.work.rpc.api.UserService;
import com.work.rpc.config.RpcServiceConfig;
import com.work.rpc.proxy.RpcClientProxy;
import com.work.rpc.server.service.UserServiceImpl;
import com.work.rpc.transmission.RpcServer;
import com.work.rpc.transmission.socket.server.SocketRpcServer;

public class Main {
    public static void main(String[] args) {
//        RpcServer rpcServer = new RpcServer() {
//            public void start() {
//                System.out.println("启动服务");
//            }
//        };
//        // 启动服务
//        rpcServer.start();
//        RpcServer rpcServer = new SocketRpcServer(8888);
//        rpcServer.start();
        UserService userServiceImpl = new UserServiceImpl();
        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig("1.0.0", "common", userServiceImpl);
        System.out.println("对应接口全类名 = " + rpcServiceConfig.rpcServiceNames());

        RpcServer rpcServer = new SocketRpcServer(8888);
        rpcServer.publishService(new RpcServiceConfig(new UserServiceImpl()));
        rpcServer.start();
//        // jdk代理：被代理类实现接口，动态代理生成这个实现类对应的接口的实现类
//        RpcClientProxy rpcClientProxy = new RpcClientProxy(new UserServiceImpl());
//        UserService userService = rpcClientProxy.getProxy();
//        User user = userService.getUser(1L);
//        System.out.println("user = " + user);
    }
}
