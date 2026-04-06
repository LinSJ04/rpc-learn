package com.work.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public enum SerializeType {
    KRYO((byte) 1, "kryo");

    private final byte code;
    private final String desc;
}
