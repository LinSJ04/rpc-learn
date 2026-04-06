package com.work.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@ToString
@Getter
@AllArgsConstructor
public enum CompressType {
    GZIP((byte) 1, "gzip");

    private final byte code;
    private final String desc;

    public static CompressType from(byte code) {
        return Arrays.stream(values())
                .filter(compressType -> compressType.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("code异常:" + code));
    }
}
