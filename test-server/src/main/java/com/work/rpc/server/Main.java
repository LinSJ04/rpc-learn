package com.work.rpc.server;

import cn.hutool.core.collection.ListUtil;
import com.work.rpc.api.UserService;
import com.work.rpc.config.RpcServiceConfig;
import com.work.rpc.dto.RpcReq;
import com.work.rpc.factory.SingletonFactory;
import com.work.rpc.loadbalance.impl.ConsistentHashLoadBalance;
import com.work.rpc.loadbalance.impl.RoundLoadBalance;
import com.work.rpc.serialize.impl.HessianSerializer;
import com.work.rpc.serialize.impl.ProtoStuffSerializer;
import com.work.rpc.transmission.netty.server.NettyRpcServer;
import com.work.rpc.server.service.UserServiceImpl;
import com.work.rpc.transmission.RpcServer;
import io.protostuff.Rpc;

import java.util.List;

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

//        ProtoStuffSerializer serializer = SingletonFactory.getInstance(ProtoStuffSerializer.class);
//
//        RpcReq rpcReq = RpcReq.builder()
//                .reqId("123123")
//                .interfaceName("qwert")
//                .parameterTypes(new Class<?>[]{String.class, Long.class})
//                .build();
//
//        byte[] data = serializer.serialize(rpcReq);
//        RpcReq req = serializer.deserialize(data, RpcReq.class);
//        System.out.println("req = " + req);

        ConsistentHashLoadBalance loadBalance = SingletonFactory.getInstance(ConsistentHashLoadBalance.class);
        RpcReq rpcReq = RpcReq.builder()
                .interfaceName("test")
                .group("group")
                .version("version")
                .build();
        List<String> list = ListUtil.of("ip1:port1", "ip2:port2", "ip3:port3");
        for (int i = 0; i < 10; i++) {
            System.out.println("select = " + loadBalance.select(list, rpcReq));
        }
    }
}
