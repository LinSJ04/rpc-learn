package com.work.rpc.api;

import com.work.rpc.annotation.Retry;

public interface UserService {
    @Retry(maxAttempts = 4, delay = 1000)
    User getUser(Long id);
}
