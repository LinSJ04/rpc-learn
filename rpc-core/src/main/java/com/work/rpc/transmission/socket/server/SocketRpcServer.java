package com.work.rpc.transmission.socket.server;

import com.work.rpc.config.RpcServiceConfig;
import com.work.rpc.constant.RpcConstant;
import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;
import com.work.rpc.factory.SingletonFactory;
import com.work.rpc.handler.RpcReqHandler;
import com.work.rpc.provider.ServiceProvider;
import com.work.rpc.provider.impl.SimpleServiceProvider;
import com.work.rpc.provider.impl.ZkServiceProvider;
import com.work.rpc.transmission.RpcServer;
import com.work.rpc.util.ThreadPoolUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

@Slf4j
public class SocketRpcServer implements RpcServer {
    private final int port;
    private final RpcReqHandler rpcReqHandler;
    private final ServiceProvider serviceProvider;
    private final ExecutorService executorService;

    public SocketRpcServer() {
        this(RpcConstant.SERVER_PORT);
    }

    public SocketRpcServer(int port) {
        this(port, SingletonFactory.getInstance(ZkServiceProvider.class));
    }

    public SocketRpcServer(int port, ServiceProvider serviceProvider) {
        this.port = port;
        this.rpcReqHandler = new RpcReqHandler(serviceProvider);
        this.serviceProvider = serviceProvider;
        this.executorService = ThreadPoolUtils.createIoIntensiveThreadPool("socket-rpc-server-");
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("服务启动，端口：{}", port);
            Socket socket;
            // serverSocket 会持续监听
            while ((socket = serverSocket.accept()) != null) {
                // 每监听到一个请求，new一个线程任务，用线程池执行线程任务即可
                executorService.submit(new SocketReqHandler(socket, rpcReqHandler));
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
