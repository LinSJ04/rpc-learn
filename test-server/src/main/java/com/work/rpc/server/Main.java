package com.work.rpc.server;

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
        RpcServer rpcServer = new SocketRpcServer(8888);
        rpcServer.start();
    }
}
