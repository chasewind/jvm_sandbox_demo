package com.deer.agent.sandbox.trace;


import com.deer.agent.sandbox.handler.TraceHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MethodSimpleRecorder {
    private static Map<String, TraceHandler>handlerMap = new ConcurrentHashMap<>();

    public static void addTraceHandler(String uniqueId,TraceHandler traceHandler){
        handlerMap.put(uniqueId,traceHandler);
    }
    public static MethodBeforeResult before(String uniqueId, Object[] request, String className, String methodName, String methodDesc) {
        MethodBeforeResult beforeResult= new MethodBeforeResult();
        TraceHandler traceHandler= handlerMap.get(uniqueId);
        if(traceHandler == null){
            return beforeResult;
        }else{
            return traceHandler.handlerBefore(request,className,methodName);
        }
    }

    public static MethodAfterResult after( Object response,String uniqueId, Object[] request, String className, String methodName, String methodDesc) {
        MethodAfterResult afterResult = new MethodAfterResult();
        TraceHandler traceHandler= handlerMap.get(uniqueId);
        if(traceHandler == null){
            return afterResult;
        }else{
            return traceHandler.handlerAfter(request,response,className,methodName);
        }
    }

}
