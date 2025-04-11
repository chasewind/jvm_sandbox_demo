package com.deer.agent;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class CommonBizInterceptor extends PreciseInterceptor{
    @Override
    public String getDescription() {
        return "普通业务所在package内方法拦截";
    }
    @RuntimeType
    public static Object intercept(@Origin Method method,  @SuperCall Callable<?> callable) throws Exception {
        try {
            //当前方法可能被放进线程中调用，也有可能直接调用
            OpenTrace openTrace = OpenTraceContext.getTrace();
            Span currentSpan = null;
            boolean isInThread = false;
            //记录为空，说明在线程中
            if (openTrace == null) {
                isInThread = true;
            }
            //以下是标准模式 入口--->上下文处理--->出口
            if(!isInThread){
                Span span = new Span();
                span.traceId = openTrace.traceId;
                span.spanId = method.getDeclaringClass().getName() + "." + method.getName();
                currentSpan = span;
                //入口
                OpenTraceContext.startDepth();
                //上下文处理
                OpenTraceContext.setSpan(currentSpan);
            }


            System.out.println("业务 "+(isInThread?"线程中间接调用 : ":"直接调用 : ") +(openTrace==null?"<--->": openTrace.traceId )+" span: "+(currentSpan==null?"<--->":currentSpan.spanId));
            // 原有函数执行
            Object ret = callable.call();
            if(!isInThread){
                //出口
                OpenTraceContext.exitDepth();
            }

            return ret;
        } catch (Exception e) {
            throw e;
        }
    }
}
