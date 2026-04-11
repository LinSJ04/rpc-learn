package com.work.rpc.server.service;

import cn.hutool.core.util.IdUtil;
import com.work.rpc.api.User;
import com.work.rpc.api.UserService;

public class UserServiceImpl implements UserService {

    public User getUser(Long id) {
        // 抛异常，测试重试策略
        int i = 1 / 0;
        return User.builder()
                .id(++id)
                .name("张三")
                .build();
    }
}
