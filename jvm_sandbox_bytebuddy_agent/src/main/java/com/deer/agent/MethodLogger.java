package com.deer.agent;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
public class MethodLogger {
    public static void logEntry(Method method, Object[] args) {

        String className = method.getDeclaringClass().getName();
        if(className.startsWith("java.lang")|| className.startsWith("java.util")){
            return;
        }
        //跳过domain session拦截
        if(className.startsWith("com.deer.base.domain")|| className.startsWith("com.deer.base.session")){
            return;
        }
        if(className.endsWith("Controller")){
            TraceRecorder.recordEntranceTrace(className,method.getName());
        }else{
            TraceRecorder.recordProcessTrace(className,method.getName());
        }
        // 获取参数名
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            String paramName = parameters[i].getName(); // 获取参数名
            Object paramValue = args[i]; // 获取参数值
            System.out.println("  Parameter name: " + paramName + ", value: " + paramValue);
        }
    }
    public static void logExit(Method method, Object returnValue) {
        String className = method.getDeclaringClass().getName();
        if(className.startsWith("java.lang")|| className.startsWith("java.util")){
            return;
        }
        //跳过domain session拦截
        if(className.startsWith("com.deer.base.domain")|| className.startsWith("com.deer.base.session")){
            return;
        }
        if(className.endsWith("Controller")){
            TraceRecorder.recordExitTrace(className,method.getName());
        }else{
            TraceRecorder.recordExitProcessTrace(className,method.getName());
        }
        System.out.println( "Return value: " + returnValue);
    }

}