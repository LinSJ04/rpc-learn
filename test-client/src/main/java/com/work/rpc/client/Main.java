package com.work.rpc.client;

import com.work.rpc.api.User;
import com.work.rpc.api.UserService;
import com.work.rpc.client.utils.ProxyUtils;
import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;
import com.work.rpc.transmission.netty.client.NettyRpcClient;
import com.work.rpc.transmission.RpcClient;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // UserServiceImpl 在 server 模块中，无法使用，需要想办法做远程调用
//        UserService userService = new UserServiceImpl();
//        User user = userService.getUser(1L);
//        System.out.println("user = " + user);

//        RpcClient rpcClient = new RpcClient() {
//            public RpcResp<?> sendReq(RpcReq req) {
//                return null;
//            }
//        };
//        RpcReq req = RpcReq.builder()
//                .reqId("123")
//                .interfaceName("com.work.rpc.api.UserService")
//                .methodName("getUser")
//                .parameters(new Object[]{1L})
//                .parameterTypes(new Class[]{Long.class})
//                .build();
//        RpcResp<?> rpcResp = rpcClient.sendReq(req);
//        User user = (User)rpcResp.getData();
//        System.out.println("user = " + user);
//        RpcClient rpcClient = new SocketRpcClient("localhost", 8888);
//        RpcReq req = RpcReq.builder()
//                            .reqId("123")
//                            .interfaceName("com.work.rpc.api.UserService")
//                            .methodName("getUser")
//                            .parameters(new Object[]{1L})
//                            .parameterTypes(new Class[]{Long.class})
//                            .build();
//        ExecutorService threadPool = ThreadPoolUtils.createIoIntensiveThreadPool("test");
//        for (int i = 0; i < 10; i++) {
//            threadPool.submit(()-> {
//                User user = (User)rpcClient.sendReq(req).getData();
//                System.out.println("user = " + user);
//            });
//        }
//        User user = (User)rpcClient.sendReq(req).getData();
//        System.out.println("user = " + user);
//        ExecutorService executorService = Executors.newFixedThreadPool(10);
//        for (int i = 0; i < 10; i++) {
//            executorService.submit(() -> {
//                User user = userService.getUser(1L);
//                System.out.println("user = " + user);
//            });
//        }
//        RpcClient rpcClient = new NettyRpcClient();
        // 目前返回为null

//        RpcResp<?> rpcResp = rpcClient.sendReq(RpcReq.builder().interfaceName("模拟请求数据").build());
//        System.out.println("rpcResp = " + rpcResp);
        // 如果之后不用代理，直接用rpcClient发送请求，就可以异步获取数据
        UserService userService = ProxyUtils.getProxy(UserService.class);
        User user = userService.getUser(1L);
        System.out.println("user = " + user);
//        // 先等1s，等令牌创建好
//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        Scanner scanner = new Scanner(System.in);
//        ExecutorService executorService = Executors.newFixedThreadPool(20);
//
//        while (true) {
//            System.out.println("请输入请求次数：");
//            int n = scanner.nextInt();
//            System.out.println("请输入请求id：");
//            long id = scanner.nextLong();
//
//            for (int i = 0; i < n; i++) {
//                executorService.execute(() -> {
//                    try {
//                        User newUser = userService.getUser(id);
//                        System.out.println("newUser = " + newUser);
//                    } catch (Exception e) {
//                        System.out.println("请求失败：" + e.getMessage());
//                    }
//                });
//            }
//        }
    }
}