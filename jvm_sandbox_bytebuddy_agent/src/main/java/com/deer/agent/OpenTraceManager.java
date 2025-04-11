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
    public static Span findLastNode(Span root) {
        if (root == null || root.childrenList.isEmpty()) {
            // 如果当前节点为空或没有子节点，返回当前节点
            return root;
        }

        // 递归查找最后一个子节点
        return findLastNode(root.childrenList.get(root.childrenList.size() - 1));
    }

}
