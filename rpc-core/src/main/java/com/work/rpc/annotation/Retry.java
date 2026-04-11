package com.work.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 自定义注解
// 什么阶段保留：一般是运行时，这个有时间可以多了解一些
@Retention(RetentionPolicy.RUNTIME)
// 可以标注在方法上
@Target(ElementType.METHOD)
public @interface Retry {
    // 对于哪些异常进行重试，默认所有异常
    // 对任何异常，比如IOException、NullPointerException或者是一些自定义的异常
    Class<? extends Throwable> value() default Exception.class;
    // 最大重试次数
    int maxAttempts() default 3;
    // 重试之间的间隔
    int delay() default 0;
}
