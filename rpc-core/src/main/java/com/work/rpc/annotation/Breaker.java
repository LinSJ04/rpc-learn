package com.work.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Breaker {
    int failThreshold() default 20;

    double successRateInHalfOpen() default 0.5;

    long windowTime() default 10;
}
