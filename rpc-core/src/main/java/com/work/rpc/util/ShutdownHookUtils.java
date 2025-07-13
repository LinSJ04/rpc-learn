package com.work.rpc.util;

import com.work.rpc.factory.SingletonFactory;
import com.work.rpc.registry.ServiceRegistry;
import com.work.rpc.registry.impl.ZkServiceRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author Mr.Pan
 * @Date 2025/2/21
 **/
@Slf4j
public class ShutdownHookUtils {
    public static void clearAll() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("系统结束运行, 清理资源");
            ServiceRegistry serviceRegistry = SingletonFactory.getInstance(ZkServiceRegistry.class);
            serviceRegistry.clearAll(); // 清理注册中心的服务
            ThreadPoolUtils.shutdownAll(); // jvm运行结束的时候关闭线程池
        }));
    }
}
