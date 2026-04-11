package com.work.rpc.api;

import com.work.rpc.annotation.Breaker;
import com.work.rpc.annotation.Retry;

public interface UserService {
    // @Retry(maxAttempts = 4, delay = 1000)
    @Breaker(windowTime = 30000) // 30s
    User getUser(Long id);
}
