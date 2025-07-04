package com.work.rpc.client;

import com.work.rpc.api.User;
import com.work.rpc.api.UserService;

public class Main {
    public static void main(String[] args) {
        // UserServiceImpl 在 server 模块中，无法使用，需要想办法做远程调用
//        UserService userService = new UserServiceImpl();
//        User user = userService.getUser(1L);
//        System.out.println("user = " + user);
    }
}
