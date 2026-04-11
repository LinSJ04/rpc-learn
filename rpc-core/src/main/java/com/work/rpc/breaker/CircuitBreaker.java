package com.work.rpc.breaker;

import java.util.concurrent.atomic.AtomicInteger;

public class CircuitBreaker {
    private State state = State.CLOSE;
    private final AtomicInteger failCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger totalCount = new AtomicInteger(0);
    // 失败阈值
    private final int failThreshold;
    // 半开状态下的成功率阈值
    private final double successRateInHalfOpen;
    // 熔断时间窗口
    private final long windowTime;
    // 上一次时间的时间
    private long lastFailTime = 0;

    public CircuitBreaker(int failThreshold, double successRateInHalfOpen, long windowTime) {
        this.failThreshold = failThreshold;
        this.successRateInHalfOpen = successRateInHalfOpen;
        this.windowTime = windowTime;
    }

    public synchronized boolean canSendReq() {
        switch (state) {
            case CLOSE:
                return true;
            case OPEN:
                if (System.currentTimeMillis() - lastFailTime <= windowTime) {
                    return false;
                }
                state = State.HALF_OPEN;
                resetCount();
                return true;
            case HALF_OPEN:
                totalCount.incrementAndGet();
                return false;
            default:
                throw new IllegalArgumentException("熔断器状态异常");
        }
    }

    public synchronized void success() {
        if (state != State.HALF_OPEN) {
            resetCount();
        }
        successCount.incrementAndGet();
        // half_open状态下，成功超过成功率阈值，切换为close状态
        if (successCount.get() >= successRateInHalfOpen * totalCount.get()) {
            state = State.CLOSE;
            resetCount();
        }
    }

    public synchronized void fail() {
        lastFailTime = System.currentTimeMillis();
        failCount.incrementAndGet();
        // half_open状态下，一失败，就切换为open状态
        if (state == State.HALF_OPEN) {
            state = State.OPEN;
        }
        if (failCount.get() >= failThreshold) {
            state = State.OPEN;
        }
    }

    private void resetCount() {
        failCount.set(0);
        successCount.set(0);
        totalCount.set(0);
    }

    enum State {
        OPEN,
        CLOSE,
        HALF_OPEN
    }
}
