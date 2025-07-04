package com.work.rpc.server;

import com.work.rpc.api.UserService;
import com.work.rpc.config.RpcServiceConfig;
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
//        UserService userServiceImpl = new UserServiceImpl();
//        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig("1.0.0", "common", userServiceImpl);
//        System.out.println("对应接口全类名 = " + rpcServiceConfig.rpcServiceNames());

        RpcServer rpcServer = new SocketRpcServer(8888);
        rpcServer.publishService(new RpcServiceConfig(new UserServiceImpl()));
        rpcServer.start();
    }
}
