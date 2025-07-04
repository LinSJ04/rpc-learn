package com.work.rpc.transmission.socket.client;

import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;
import com.work.rpc.transmission.RpcClient;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class SocketRpcClient implements RpcClient {
    private String host;
    private int port;

    public SocketRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public RpcResp<?> sendReq(RpcReq req) {
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(req);
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
