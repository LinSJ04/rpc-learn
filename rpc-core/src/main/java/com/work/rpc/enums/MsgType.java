package com.work.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public enum MsgType {
    HEARTBEAT_REQ((byte) 1, "心跳请求"),
    HEARTBEAT_RESP((byte) 2, "心跳响应"),
    RPC_REQ((byte) 3, "RPC请求"),
    RPC_RESP((byte) 4, "RPC响应");


    private final byte code;
    private final String desc;

    public static boolean isHeartbeat(MsgType msgType) {
        return msgType == HEARTBEAT_REQ || msgType == HEARTBEAT_RESP;
    }
}
