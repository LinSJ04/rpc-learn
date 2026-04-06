package com.work.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

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

    public boolean isHeartbeat() {
        return this == HEARTBEAT_REQ || this == HEARTBEAT_RESP;
    }

    public boolean isReq() {
        return this == RPC_REQ || this == HEARTBEAT_REQ;
    }

    public static MsgType from(byte code) {
        return Arrays.stream(values())
                .filter(msgType -> msgType.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("code异常:" + code));
    }
}
