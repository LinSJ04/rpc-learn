package com.work.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RpcRespStatus { // 枚举只需要get和toString
    SUCCESS(0, "success"),
    FAIL(9999, "fail"),
    ;

    private final int code;
    private final String msg;
}
