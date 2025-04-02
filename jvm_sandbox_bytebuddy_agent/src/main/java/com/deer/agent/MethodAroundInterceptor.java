package com.deer.agent;


import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.Callable;

public class MethodAroundInterceptor {

    @RuntimeType
    public static Object intercept(@Origin Method method, @AllArguments Object[] args, @SuperCall Callable<?> callable) throws Exception {
        long start = System.nanoTime();
        try {
            System.out.println("Entering method: " + method.toGenericString());
            // 获取参数名
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                String paramName = parameters[i].getName(); // 获取参数名
                Object paramValue = args[i]; // 获取参数值
                System.out.println("Parameter name: " + paramName + ", value: " + paramValue);
            }
            // 原有函数执行
            Object ret = callable.call();
            System.out.println("Return value: " + ret);

            return ret;
        } finally {
            System.out.println(method + " 方法耗时： " + (System.nanoTime() - start)/1000 + "微秒");
        }
    }

}
