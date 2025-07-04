package com.work.rpc.util;

import com.pig.rpc.factory.SingletonFactory;
import com.pig.rpc.registry.ServiceRegistry;
import com.pig.rpc.registry.impl.ZkServiceRegistry;
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
            ThreadPoolUtils.shutdownAll(); // jvm运行结束的时候关闭线程池
        }));
    }
}
