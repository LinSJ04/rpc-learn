package com.work.rpc.constant;

public class RpcConstant {
    public static final int SERVER_PORT = 9999; // 服务端口号

    public static final String ZK_IP = "192.168.230.131"; // Zookeeper地址

    public static final int ZK_PORT = 2181; // Zookeeper端口号

    public static final String ZK_RPC_ROOT_PATH = "/rpc"; // Zookeeper根路径


    // netty
    public static final String NETTY_RPC_KEY = "RpcResp";
    public static final byte[] PRC_MAGIC_CODE = new byte[]{(byte) 'l', (byte) 'r', (byte) 'p', (byte) 'c'};
    // 请求头长度
    public static final int REQ_HEAD_LEN = 16; // 16B
    // 总长度 full len
    public static final int REQ_MAX_LEN = 1024 * 1024; // 1MB
}
