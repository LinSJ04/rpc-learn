package com.work.rpc.transmission.socket.server;

import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;
import com.work.rpc.transmission.RpcServer;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class SocketRpcServer implements RpcServer {
    private int port;

    public SocketRpcServer(int port) {
        this.port = port;
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
                String data = "模拟调用成功获得的数据";

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                RpcResp<String> rpcResp = RpcResp.success(rpcReq.getReqId(), data);
                objectOutputStream.writeObject(rpcResp);
                objectOutputStream.flush();
            }
        } catch (Exception e) {
            log.error("服务端异常", e);
        }
    }
}
