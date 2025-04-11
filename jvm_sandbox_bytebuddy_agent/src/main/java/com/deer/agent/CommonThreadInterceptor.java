package com.deer.agent;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Callable;

public class CommonThreadInterceptor extends PreciseInterceptor{
    @Override
    public String getDescription() {
        return "拦截线程对应的Runnable Callable";
    }

    @RuntimeType
    public static void intercept(@This Object obj, @Origin Method method, @SuperCall Callable<?> callable) throws Exception {

        System.out.println("Intercepting method: " + method.getName() + " of class: " + obj.getClass().getCanonicalName());

        try {
//             获取 traceId 字段
            java.lang.reflect.Field field = method.getDeclaringClass().getDeclaredField("traceId");
            field.setAccessible(true);
            String traceId = (String) field.get(obj);
            System.out.println("no loss traceId: " + traceId);
            //这时候切换线程数据都丢了,因此需要从这里重新找回原来的数据做简单补充
            OpenTrace openTrace = OpenTraceManager.getTrace(traceId);
            if(openTrace!=null&&openTrace.firstSpan!=null){
                Span lastChild =  OpenTraceManager.findLastNode(openTrace.firstSpan);
                if(lastChild != null){
                    Span span = new Span();
                    span.traceId = openTrace.traceId;
                    span.spanId = method.getDeclaringClass().getName() + "." + method.getName();
                    lastChild.childrenList.add(span);
                }
            }
            // 原有函数执行
               callable.call();
        } catch (Exception e) {
            throw e;
        }
    }
}
