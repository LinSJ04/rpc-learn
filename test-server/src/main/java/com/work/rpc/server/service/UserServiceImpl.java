package com.work.rpc.server.service;

import com.work.rpc.annotation.Limit;
import com.work.rpc.api.User;
import com.work.rpc.api.UserService;

public class UserServiceImpl implements UserService {
    // @Limit(permitsPerSecond = 5, timeout = 0)
    public User getUser(Long id) {
        if (id < 0) {
            throw new IllegalArgumentException("id不能小于0");
        }
        return User.builder()
                .id(id)
                .name("张三")
                .build();
    }
}
