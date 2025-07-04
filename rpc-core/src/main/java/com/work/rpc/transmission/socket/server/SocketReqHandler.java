package com.work.rpc.transmission.socket.server;

import com.work.rpc.dto.RpcReq;
import com.work.rpc.dto.RpcResp;
import com.work.rpc.handler.RpcReqHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
@AllArgsConstructor
public class SocketReqHandler implements Runnable{ // 并行任务交给线程池处理
    private final Socket socket;
    private final RpcReqHandler rpcReqHandler;

    @SneakyThrows
    @Override
    public void run() {
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        RpcReq rpcReq = (RpcReq) objectInputStream.readObject();
        System.out.println("rpcReq = " + rpcReq);

        // 调用方法处理请求
        Object data = rpcReqHandler.invoke(rpcReq);

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        RpcResp<?> rpcResp = RpcResp.success(rpcReq.getReqId(), data);
        objectOutputStream.writeObject(rpcResp);
        objectOutputStream.flush();
    }
}