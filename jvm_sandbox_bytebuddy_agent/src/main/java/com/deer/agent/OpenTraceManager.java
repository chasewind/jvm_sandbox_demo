package com.deer.agent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OpenTraceManager {

    private static Map<String, OpenTrace> traceMap = new ConcurrentHashMap<>();

    public static void addTrace(String traceId, OpenTrace trace) {
        traceMap.put(traceId, trace);
    }

    public static OpenTrace getTrace(String traceId) {
        return traceMap.get(traceId);
    }

}
