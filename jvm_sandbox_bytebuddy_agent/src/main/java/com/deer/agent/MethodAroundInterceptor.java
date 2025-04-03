package com.deer.agent;


import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.UUID;
import java.util.concurrent.Callable;

public class MethodAroundInterceptor {
    @RuntimeType
    public static Object intercept(@Origin Method method, @AllArguments Object[] args, @SuperCall Callable<?> callable) throws Exception {
        try {
            MethodLogger.logEntry(method, args); // 记录方法进入
            String linkId = TrackManager.getCurrentSpan();
            if (null == linkId) {
                linkId = UUID.randomUUID().toString();
                TrackContext.setLinkId(linkId);
            }
            String entrySpan = TrackManager.createEntrySpan();
            System.out.println("链路追踪：" + entrySpan + " " + method.getDeclaringClass().getName() + "." + method.getName());
            // 原有函数执行
            Object ret = callable.call();
            MethodLogger.logExit(method, ret); // 记录方法退出
            TrackManager.getExitSpan();
            return ret;
        } catch (Exception e) {
            throw e;
        }
    }

}
