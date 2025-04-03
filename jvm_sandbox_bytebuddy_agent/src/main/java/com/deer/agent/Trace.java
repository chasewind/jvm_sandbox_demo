package com.deer.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trace {
    public int depth = 0;
    public int sequence = 0;
    Map<Integer, List<String>> map = new HashMap<>();
    Map<Integer,String> routeMap = new HashMap<>();

    public void before() {
        depth++;
        sequence++;
    }

    public void after() {
        depth--;
    }

    public void record(String className, String methodName) {
        map.computeIfAbsent(depth, k -> new ArrayList<>()).add(className + "#" + methodName);
        routeMap.put(sequence,className + "#" + methodName);
    }
}
