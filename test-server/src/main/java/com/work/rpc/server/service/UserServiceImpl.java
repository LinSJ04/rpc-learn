package com.work.rpc.server.service;

import cn.hutool.core.util.IdUtil;
import com.work.rpc.api.User;
import com.work.rpc.api.UserService;

public class UserServiceImpl implements UserService {

    public User getUser(Long id) {
        return User.builder()
                .id(++id)
                .name("张三")
                .build();
    }
}
