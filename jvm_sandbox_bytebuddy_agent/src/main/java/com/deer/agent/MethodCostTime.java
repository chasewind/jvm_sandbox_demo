package com.deer.agent;


import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class MethodCostTime {

    @RuntimeType
    public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) throws Exception {
        long start = System.nanoTime();
        try {
            // 原有函数执行
            return callable.call();
        } finally {
            System.out.println(method + " 方法耗时： " + (System.nanoTime() - start)/1000 + "微秒");
        }
    }

}
