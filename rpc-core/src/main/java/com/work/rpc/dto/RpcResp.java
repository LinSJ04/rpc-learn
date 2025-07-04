package com.work.rpc.dto;

import com.work.rpc.enums.RpcRespStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RpcResp<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reqId;
    private String msg;
    private Integer code;
    private T data;

    public static <T> RpcResp<T> success(String reqId, T data) {
        return RpcResp.<T>builder()
                .reqId(reqId)
                .code(0)
                .data(data)
                .build();
    }

    public static <T> RpcResp<T> fail(String reqId, RpcRespStatus status) {
        return RpcResp.<T>builder()
                .reqId(reqId)
                .code(status.getCode())
                .msg(status.getMsg())
                .build();
    }

    public static <T> RpcResp<T> fail(String reqId, String msg) {
        return RpcResp.<T>builder()
                .reqId(reqId)
                .code(RpcRespStatus.FAIL.getCode())
                .msg(msg)
                .build();
    }
}
