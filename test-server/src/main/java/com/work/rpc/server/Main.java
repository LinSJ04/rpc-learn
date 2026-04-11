package com.work.rpc.server;

import com.work.rpc.api.UserService;
import com.work.rpc.config.RpcServiceConfig;
import com.work.rpc.dto.RpcReq;
import com.work.rpc.factory.SingletonFactory;
import com.work.rpc.serialize.impl.HessianSerializer;
import com.work.rpc.serialize.impl.ProtoStuffSerializer;
import com.work.rpc.transmission.netty.server.NettyRpcServer;
import com.work.rpc.server.service.UserServiceImpl;
import com.work.rpc.transmission.RpcServer;
import io.protostuff.Rpc;

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


//        RpcServer rpcServer = new SocketRpcServer();
//        rpcServer.publishService(new RpcServiceConfig(new UserServiceImpl()));
//        rpcServer.start();

//        // jdk代理：被代理类实现接口，动态代理生成这个实现类对应的接口的实现类
//        RpcClientProxy rpcClientProxy = new RpcClientProxy(new UserServiceImpl());
//        UserService userService = rpcClientProxy.getProxy();
//        User user = userService.getUser(1L);
//        System.out.println("user = " + user);
//        UserService userServiceImpl = new UserServiceImpl();
//        RpcServiceConfig rpcServiceConfig = new RpcServiceConfig(userServiceImpl);
//        System.out.println("对应接口全类名 = " + rpcServiceConfig.rpcServiceNames());
//        RpcServer rpcServer = new NettyRpcServer();
//        rpcServer.publishService(rpcServiceConfig);
//        rpcServer.start();

        ProtoStuffSerializer serializer = SingletonFactory.getInstance(ProtoStuffSerializer.class);

        RpcReq rpcReq = RpcReq.builder()
                .reqId("123123")
                .interfaceName("qwert")
                .parameterTypes(new Class<?>[]{String.class, Long.class})
                .build();

        byte[] data = serializer.serialize(rpcReq);
        RpcReq req = serializer.deserialize(data, RpcReq.class);
        System.out.println("req = " + req);
    }
}
