package com.work.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@ToString
@AllArgsConstructor
@Getter
public enum VersionType {
    VERSION1((byte) 1, "版本1");

    private final byte code;
    private final String desc;

    public static VersionType from(byte code) {
        return Arrays.stream(values())
                .filter(versionType -> versionType.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("code异常:" + code));
    }
}
