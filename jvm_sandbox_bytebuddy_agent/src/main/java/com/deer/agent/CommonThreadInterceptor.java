package com.deer.agent;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Callable;

public class CommonThreadInterceptor extends PreciseInterceptor{
    @Override
    public String getDescription() {
        return "拦截线程对应的Runnable Callable";
    }
    @RuntimeType
    public static Object intercept(@Advice.This Object obj, @Origin Method method, @SuperCall Callable<?> callable) throws Exception {
        System.out.println("Intercepting method: " + method.getName() + " of class: " + obj.getClass().getName());

        try {
            // 获取 traceId 字段并设置值
            java.lang.reflect.Field field = obj.getClass().getDeclaredField("traceId");
            field.setAccessible(true); // 设置可访问性
            String traceId = (String) field.get(obj); // 获取 traceId 的值
            System.out.println("traceId: " + traceId); // 输出 traceId 的值
            // 原有函数执行
              callable.call();
              return null;
        } catch (Exception e) {
            throw e;
        }
    }
}
