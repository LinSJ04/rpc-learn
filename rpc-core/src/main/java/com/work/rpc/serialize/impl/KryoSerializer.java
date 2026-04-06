package com.work.rpc.serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.work.rpc.dto.RpcMsg;
import com.work.rpc.dto.RpcResp;
import com.work.rpc.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Slf4j
public class KryoSerializer implements Serializer {
    // Kryo本身不是线程安全的
    // ThreadLocal 的作用是什么？不用频繁set和get？
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcMsg.class);
        kryo.register(RpcResp.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream oos = new ByteArrayOutputStream();
             Output output = new Output(oos)) {
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            // 把对象序列化后放到流里
            kryo.writeObject(output, obj);
            output.flush();
            return oos.toByteArray();
        } catch (Exception e) {
            log.error("kryo序列化失败", e);
            throw new RuntimeException(e);
        } finally {
            KRYO_THREAD_LOCAL.remove();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes);
             Input input = new Input(is)) {
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            return kryo.readObject(input, clazz);
        } catch (Exception e) {
            log.error("kryo反序列化失败", e);
            throw new RuntimeException(e);
        } finally {
            KRYO_THREAD_LOCAL.remove();
        }
    }
}
