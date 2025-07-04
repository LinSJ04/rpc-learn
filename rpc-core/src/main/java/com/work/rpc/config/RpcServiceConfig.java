package com.work.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcServiceConfig { // 服务发布的配置类
    private String version = "";
    private String group = "";
    private Object service; // 接口的实现类

    public RpcServiceConfig(Object service) {
        this.service = service;
    }

    public List<String> rpcServiceNames() {
        List<String> interfaceNames = interfaceNames();
        return interfaceNames.stream()
                .map(interfaceName -> interfaceName + getVersion() + getGroup())
                .collect(Collectors.toList());
    }

    private List<String> interfaceNames() {
        // 一个接口的实现类 它可能同时实现了别的接口
        // 获取接口的全类名
        return Arrays.stream(service.getClass().getInterfaces())
                .map(Class::getCanonicalName)
                .collect(Collectors.toList());
    }
}
