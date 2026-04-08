package com.work.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

// 自定义注解，可以标注在方法上
@Target(ElementType.METHOD)
public @interface Retry {
}
