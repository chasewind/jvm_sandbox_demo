package com.deer.agent.sandbox.handler;

import com.deer.agent.sandbox.trace.MethodAfterResult;
import com.deer.agent.sandbox.trace.MethodBeforeResult;

/**
 * 默认处理器，什么都不做，直接放行
 */
public class NopTraceHandler implements TraceHandler {
    @Override
    public MethodBeforeResult handlerBefore(Object[] request, String className, String methodName) {
        MethodBeforeResult beforeResult = new MethodBeforeResult();
        beforeResult.needContinue = true;
        return beforeResult;
    }

    @Override
    public MethodAfterResult handlerAfter(Object[] request, Object response, String className, String methodName) {
        MethodAfterResult afterResult = new MethodAfterResult();
        afterResult.needContinue = true;
        afterResult.response = response;
        return afterResult;
    }
}
