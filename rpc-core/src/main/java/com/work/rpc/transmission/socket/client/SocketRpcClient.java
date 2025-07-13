package com.work.rpc.transmission.socket.client;

import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;
import com.work.rpc.factory.SingletonFactory;
import com.work.rpc.registry.ServiceDiscovery;
import com.work.rpc.registry.impl.ZkServiceDiscovery;
import com.work.rpc.transmission.RpcClient;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class SocketRpcClient implements RpcClient {
    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this(SingletonFactory.getInstance(ZkServiceDiscovery.class));
    }

    public SocketRpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public RpcResp<?> sendReq(RpcReq rpcReq) {
        // 直接从注册中心获取到服务的地址
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcReq);
        try (Socket socket = new Socket(inetSocketAddress.getAddress(), inetSocketAddress.getPort())) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(rpcReq);
            // flush()强制将缓冲区中的任何尚未写出的数据立即写入到目标设备或文件中
            objectOutputStream.flush();

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return (RpcResp<?>) objectInputStream.readObject();
        } catch (Exception e) {
            log.error("发送rpc请求失败", e);
        }
        return null;
    }
}
