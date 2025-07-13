package com.work.rpc.registry.zk;

import cn.hutool.core.util.StrUtil;
import com.work.rpc.constant.RpcConstant;
import com.work.rpc.util.IPUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.checkerframework.checker.units.qual.C;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Zookeeper客户端
 * 用于与Zookeeper进行交互
 * 主要用于服务注册和发现
 */
@Slf4j
public class ZkClient {
    private static final int BASE_SLEEP_TIME = 1000; // 重试之间等待的初始时间
    private static final int MAX_RETRIES = 3; // 最大重试次数
    private final CuratorFramework client;
    // key存的是/rpc/rpcServiceName List存的是地址列表
    private static final Map<String, List<String>> SERVICE_ADDRESS_CACHE = new ConcurrentHashMap<>();
    // 用ConcurrentHashMap.newKeySet()来创建一个线程安全的Set key存的是/rpc/rpcServiceName/host:port
    private static final Set<String> SERVICE_ADDRESS_SET = ConcurrentHashMap.newKeySet();

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
        if (SERVICE_ADDRESS_SET.contains(path)) {
            log.info("节点已存在: {}", path);
            return;
        }
        if (client.checkExists().forPath(path) != null) {
            // 把zk的加到本地缓存中
            SERVICE_ADDRESS_SET.add(path);
            log.info("节点已存在: {}", path);
            return;
        }
        log.info("创建节点: {}", path);
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path);
        SERVICE_ADDRESS_SET.add(path);
    }

    @SneakyThrows
    public List<String> getChildrenNodes(String path) {
        if (StrUtil.isBlank(path)) {
            throw new IllegalArgumentException("path不能为空");
        }
        if (SERVICE_ADDRESS_CACHE.containsKey(path)) {
            log.info("从缓存中获取子节点: {}", path);
            return SERVICE_ADDRESS_CACHE.get(path);
        }
        List<String> children = client.getChildren().forPath(path);
        SERVICE_ADDRESS_CACHE.put(path, children);
        watchNode(path); // 只有新建的时候，才会注册监听，不会多次注册监听
        return children;
    }

    public void clearAll(InetSocketAddress address) {
        if (Objects.isNull(address)) {
            throw new IllegalArgumentException("address不能为空");
        }

        SERVICE_ADDRESS_SET.forEach(path -> {
            if (path.endsWith(IPUtils.tpIpPort(address))) {
                log.debug("删除zk节点：{}", path);
                try {
                    client.delete()
                            .deletingChildrenIfNeeded()
                            .forPath(path);
                    // 此处清空指的是服务关闭了
                } catch (Exception e) {
                    log.debug("zk删除节点失败: {}", path, e);
                }
            }
        });
    }

    @SneakyThrows
    private void watchNode(String path) {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
        // 给节点注册子节点监听器
        PathChildrenCacheListener pathChildrenCacheListener = (curClient, event) -> {
            List<String> children = curClient.getChildren().forPath(path);
            SERVICE_ADDRESS_CACHE.put(path, children);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }
}
