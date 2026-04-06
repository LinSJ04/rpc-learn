package com.work.rpc.compress;

public interface Compress {
    byte[] compress(byte[] bytes);
    byte[] decompress(byte[] bytes);
}
