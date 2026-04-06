package com.work.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public enum CompressType {
    GZIP((byte) 1, "gzip");

    private final byte code;
    private final String desc;
}
