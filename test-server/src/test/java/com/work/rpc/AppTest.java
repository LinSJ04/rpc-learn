package com.work.rpc;

import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;

/**
 * Unit test for simple App.
 */
public class AppTest
{
    // 重试之间等待的初始时间
    private static final int BASE_SLEEP_TIME = 1000;
    // 最大重试次数
    private static final int MAX_RETRIES = 3;

    @SneakyThrows
    public static void main(String[] args) {

        // 重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);

        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                // 要连接的服务器列表
                .connectString("192.168.91.100:2181")
                .retryPolicy(retryPolicy)
                .build();

        zkClient.start();

//        // 创建持久化节点 (默认就是持久化的), 父节点不存在时报错
//        zkClient.create().forPath("/node1");  // 创建node1父节点
//        zkClient.create().forPath("/node1/00001");
//        zkClient.create().withMode(CreateMode.PERSISTENT).forPath("/node1/00002");

//        // 当父节点不存在时, 会自动创建父节点 更推荐使用
//        zkClient.create()
//                .creatingParentsIfNeeded()
//                .withMode(CreateMode.PERSISTENT)
//                .forPath("/node1/00002", "测试数据".getBytes(StandardCharsets.UTF_8));
//
//        //不为null的话，说明节点创建成功
//        Stat stat = zkClient.checkExists().forPath("/node1/00002");
//
//        if (stat != null) {
//            System.out.println("节点创建成功，节点信息：" + stat);
//        } else {
//            System.out.println("节点创建失败");
//        }
//
//        // 获取节点的数据内容，获取到的是 byte数组
//        byte[] bytes = zkClient.getData().forPath("/node1/00002");
//        System.out.println(new String(bytes));
//
//        zkClient.setData().forPath("/node1/00002","测试更新数据".getBytes());
//
//        // 获取更新后的节点数据
//        byte[] updatedBytes = zkClient.getData().forPath("/node1/00002");
//        System.out.println("更新后的节点数据：" + new String(updatedBytes));
//
        // 删除节点及其所有子节点
//        zkClient.delete().deletingChildrenIfNeeded().forPath("/node1");

        // 需要创建zkclient

        // 监听节点的数据变化
//        String path = "/n1";
//
//        // 创建 NodeCache 实例
//        NodeCache nodeCache = new NodeCache(zkClient, path);
//
//        // 注册监听器
//        NodeCacheListener listener = () -> {
//            if (nodeCache.getCurrentData() != null) {
//                String data = new String(nodeCache.getCurrentData().getData());
//                System.out.println("节点数据变化: " + data);
//            } else {
//                System.out.println("节点被删除");
//            }
//        };
//        nodeCache.getListenable().addListener(listener);
//
//        // 启动 NodeCache
//        nodeCache.start();
//
//        // 模拟程序运行一段时间
//        System.in.read(); // 把代码运行阻塞住，否则监听器的效果就看不到了

        // 监听子节点状态变化
        // 只能监听当前节点的子节点变化，不能监听当前节点本身的数据变化，包括孙子节点也不能
        // 需要创建zkclient

//        String path = "/node1";
//
//        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, path, true);
//
//        // 给某个节点注册子节点监听器
//        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
//            if (pathChildrenCacheEvent.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
//                System.out.println("子节点删除");
//            } else if (pathChildrenCacheEvent.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
//                System.out.println("子节点新增");
//            } else if (pathChildrenCacheEvent.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
//                System.out.println("子节点修改");
//            }
//        };
//
//        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
//        // 启动pathChildrenCache
//        pathChildrenCache.start();
//
//        // 模拟程序运行一段时间
//        System.in.read(); // 把代码运行阻塞住，否则监听器的效果就看不到了

        // 监听节点的状态变化 可以监听到以当前节点为根的所有子节点的变化
        // 需要创建zkclient

        String path = "/node1";

        // 创建 TreeCache 实例
        TreeCache treeCache = new TreeCache(zkClient, path);

        // 注册监听器
        TreeCacheListener listener = (curatorFramework, treeCacheEvent) -> {
            switch (treeCacheEvent.getType()) {
                case NODE_ADDED:
                    System.out.println("Node added: " + treeCacheEvent.getData().getPath());
                    break;
                case NODE_REMOVED:
                    System.out.println("Node removed: " + treeCacheEvent.getData().getPath());
                    break;
                case NODE_UPDATED:
                    System.out.println("Node updated: " + treeCacheEvent.getData().getPath());
                    break;
            }
        };
        treeCache.getListenable().addListener(listener);

        // 启动 TreeCache
        treeCache.start();

        // 模拟程序运行一段时间
        System.in.read(); // 把代码运行阻塞住，否则监听器的效果就看不到了
    }
}
