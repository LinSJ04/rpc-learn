package com.work.rpc.registry.zk;

import cn.hutool.core.util.StrUtil;
import com.work.rpc.constant.RpcConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * Zookeeper客户端
 * 用于与Zookeeper进行交互
 * 主要用于服务注册和发现
 */
@Slf4j
public class ZkClient {
    private static final int BASE_SLEEP_TIME = 1000; // 重试之间等待的初始时间
    private static final int MAX_RETRIES = 3; // 最大重试次数
    private CuratorFramework client;

    public ZkClient() {
        this(RpcConstant.ZK_IP, RpcConstant.ZK_PORT);
    }

    public ZkClient(String hostname, int port) {
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        this.client = CuratorFrameworkFactory.builder()
                .connectString(hostname + StrUtil.COLON + port)
                .retryPolicy(retry)
                .build();
        log.info("开始连接zk...");
        this.client.start();
        log.info("zk连接成功");
    }

    @SneakyThrows
    public void createPersistentNode(String path) {
        if (StrUtil.isBlank(path)) {
            throw new IllegalArgumentException("path不能为空");
        }
        if (client.checkExists().forPath(path) != null) {
            log.info("节点已存在: {}", path);
            return;
        }
        log.info("创建节点: {}", path);
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path);
    }

    @SneakyThrows
    public List<String> getChildrenNodes(String path) {
        if (StrUtil.isBlank(path)) {
            throw new IllegalArgumentException("path不能为空");
        }
        return client.getChildren().forPath(path);
    }
}
