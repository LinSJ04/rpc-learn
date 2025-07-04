package com.work.rpc.transmission.socket.server;

import com.work.rpc.config.RpcServiceConfig;
import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;
import com.work.rpc.handler.RpcReqHandler;
import com.work.rpc.provider.ServiceProvider;
import com.work.rpc.provider.impl.SimpleServiceProvider;
import com.work.rpc.transmission.RpcServer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class SocketRpcServer implements RpcServer {
    private final int port;
    private final RpcReqHandler rpcReqHandler;

    private final ServiceProvider serviceProvider;

    public SocketRpcServer(int port) {
        this(port, new SimpleServiceProvider());
    }

    public SocketRpcServer(int port, ServiceProvider serviceProvider) {
        this.port = port;
        this.rpcReqHandler = new RpcReqHandler(serviceProvider);
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("服务启动，端口：{}", port);
            Socket socket;
            // serverSocket 会持续监听
            while ((socket = serverSocket.accept()) != null) {
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                RpcReq rpcReq = (RpcReq) objectInputStream.readObject();
                System.out.println("rpcReq = " + rpcReq);

                // 假设调用了req中的接口实现类的方法
//                String data = "模拟调用成功获得的数据";
                Object data = rpcReqHandler.invoke(rpcReq);

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                RpcResp<?> rpcResp = RpcResp.success(rpcReq.getReqId(), data);
                objectOutputStream.writeObject(rpcResp);
                objectOutputStream.flush();
            }
        } catch (Exception e) {
            log.error("服务端异常", e);
        }
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }
}
