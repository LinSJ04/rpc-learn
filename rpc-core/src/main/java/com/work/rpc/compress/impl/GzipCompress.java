package com.work.rpc.compress.impl;

import cn.hutool.core.util.ZipUtil;
import com.work.rpc.compress.Compress;

import java.util.Objects;


public class GzipCompress implements Compress {
    @Override
    public byte[] compress(byte[] bytes) {
        if (Objects.isNull(bytes) || bytes.length == 0) {
            return bytes;
        }
        return ZipUtil.gzip(bytes);
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if (Objects.isNull(bytes) || bytes.length == 0) {
            return bytes;
        }
        return ZipUtil.unGzip(bytes);
    }
}
