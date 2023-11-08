package com.deer.agent.sandbox.handler;

import com.deer.agent.sandbox.trace.MethodAfterResult;
import com.deer.agent.sandbox.trace.MethodBeforeResult;

public interface TraceHandler {
    MethodBeforeResult handlerBefore(Object[] request, String className, String methodName);

    MethodAfterResult handlerAfter(Object[] request, Object response, String className, String methodName);
}
