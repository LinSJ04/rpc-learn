package com.work.rpc.util;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public final class ThreadPoolUtils {
    private static final Map<String, ExecutorService> THREAD_POOL_CACHE = new ConcurrentHashMap<>();
    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors(); // 获取CPU数量
    private static final int CPU_INTENSIVE_NUM = CPU_NUM + 1; // CPU密集型
    private static final int IO_INTENSIVE_NUM = CPU_NUM * 2; // IO密集型
    private static final int DEFAULT_KEEP_ALIVE_TIME = 60; // 默认的空闲时间
    private static final int DEFAULT_QUEUE_SIZE = 128; // 默认阻塞队列大小

    // CPU密集型的线程池
    public static ExecutorService createCpuIntensiveThreadPool(String poolName) {
        return createThreadPool(CPU_INTENSIVE_NUM, poolName);
    }

    // IO密集型的线程池
    public static ExecutorService createIoIntensiveThreadPool(String poolName) {
        return createThreadPool(IO_INTENSIVE_NUM, poolName);
    }

    // 没有临时的线程
    public static ExecutorService createThreadPool(
        int corePoolSize,
        String poolName
    ) {
        return createThreadPool(corePoolSize, corePoolSize, poolName);
    }

    // 有临时的线程
    public static ExecutorService createThreadPool(
        int corePoolSize,
        int maxPoolSize,
        String poolName
    ) {
        return createThreadPool(corePoolSize, maxPoolSize, DEFAULT_KEEP_ALIVE_TIME, DEFAULT_QUEUE_SIZE, poolName);
    }

    // 自定义存活时间和阻塞队列大小
    public static ExecutorService createThreadPool(
        int corePoolSize,
        int maxPoolSize,
        long keepAliveTime,
        int queueSize,
        String poolName
    ) {
        return createThreadPool(corePoolSize, maxPoolSize, keepAliveTime, queueSize, poolName, false);
    }

    // 创建线程池
    public static ExecutorService createThreadPool(
        int corePoolSize,
        int maxPoolSize,
        long keepAliveTime,
        int queueSize,
        String poolName,
        boolean isDaemon
    ) {
        if (THREAD_POOL_CACHE.containsKey(poolName)) {
            return THREAD_POOL_CACHE.get(poolName);
        }

        ExecutorService executorService = new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            keepAliveTime,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(queueSize),
            createThreadFactory(poolName, isDaemon)
        );

        log.debug("创建线程池: {}", poolName);

        THREAD_POOL_CACHE.put(poolName, executorService);
        return executorService;
    }

    // 默认不用守护线程
    public static ThreadFactory createThreadFactory(String poolName) {
        return createThreadFactory(poolName, false);
    }

    public static ThreadFactory createThreadFactory(String poolName, boolean isDaemon) {
        // 守护线程：垃圾回收、心跳保持，优先级较低
        ThreadFactoryBuilder threadFactoryBuilder = ThreadFactoryBuilder.create()
            .setDaemon(isDaemon);

        if (StrUtil.isBlank(poolName)) {
            return threadFactoryBuilder.build();
        }

        return threadFactoryBuilder.setNamePrefix(poolName)
            .build();
    }

    // 关闭线程池
    public static void shutdownAll() {
        // 比较慢，并行做(parallelStream)
        THREAD_POOL_CACHE.entrySet().parallelStream()
            .forEach(entry -> {
                String poolName = entry.getKey();
                ExecutorService executorService = entry.getValue();

                executorService.shutdown();
                log.info("{}, 线程池开始停止...", poolName);

                try {
                    if (executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                        log.info("{}, 线程池已停止", poolName);
                    } else {
                        log.info("{}, 线程池10s内未停止, 强制停止", poolName);
                        executorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    log.error("{}, 线程池停止异常", poolName);
                    executorService.shutdownNow();
                }
            });
    }
}
