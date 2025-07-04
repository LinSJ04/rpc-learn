package com.work.rpc.transmission.socket.server;

import com.work.rpc.config.RpcServiceConfig;
import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;
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
    private int port;
    private ServiceProvider serviceProvider;

    public SocketRpcServer(int port) {
        this.port = port;
        this.serviceProvider = new SimpleServiceProvider();
    }

    public SocketRpcServer(int port, ServiceProvider serviceProvider) {
        this.port = port;
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
                Object data = invoke(rpcReq);

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

    @SneakyThrows // Lombok, 相当于帮忙做了try catch
    private Object invoke(RpcReq rpcReq) {
        String rpcServiceName = rpcReq.rpcServiceName();
        Object service = serviceProvider.getService(rpcServiceName);

        // 获取到全类名
        log.debug("获取到对应服务：{}", service.getClass().getCanonicalName());
        // 使用反射获取Class对象
        return service.getClass()
                .getMethod(rpcReq.getMethodName(), rpcReq.getParameterTypes())
                .invoke(service, rpcReq.getParameters());
    }
}
