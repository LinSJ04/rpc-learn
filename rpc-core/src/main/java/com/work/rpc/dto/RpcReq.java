package com.work.rpc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcReq implements Serializable { // 网络中传输需要序列化，所以需要实现Serializable接口
    private static final long serialVersionUID = 1L;

    private String reqId;
    private String interfaceName; // 要调用的接口名 需要使用的实际上是这个接口的实现类
    private String methodName; // 方法名
    private Class<?>[] parameterTypes; // 参数类型
    private Object[] parameters; // 参数
    private String version; // 版本，迭代的版本
    private String group; // 分组，普通用户/管理用户等

    // UserService -> CommonUserServiceImpl1.getUser()
    // UserService -> CommonUserServiceImpl2.getUser()
    // UserService -> AdminUserServiceImpl1.getUser()
    // UserService -> AdminUserServiceImpl2.getUser()

}
