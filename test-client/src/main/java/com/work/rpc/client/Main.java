package com.work.rpc.client;

import com.work.rpc.api.User;
import com.work.rpc.api.UserService;
import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;
import com.work.rpc.transmission.RpcClient;
import com.work.rpc.transmission.socket.client.SocketRpcClient;

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
        RpcClient rpcClient = new SocketRpcClient("localhost", 8888);
        RpcReq req = RpcReq.builder()
                            .reqId("123")
                            .interfaceName("com.work.rpc.api.UserService")
                            .methodName("getUser")
                            .parameters(new Object[]{1L})
                            .parameterTypes(new Class[]{Long.class})
                            .build();
        RpcResp<?> rpcResp = rpcClient.sendReq(req);
        Object data = rpcResp.getData();
        System.out.println("data = " + data);
    }
}
