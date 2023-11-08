package com.deer.agent.sandbox;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 计算耗时，fullMethodName = className.methodName
 */
public class TimeUtil {

    public static Map<String, Long> sStartTime = new HashMap<>();
    public static Map<String, Long> sEndTime = new HashMap<>();

    public static void setStartTime(String fullMethodName, long time) {
        sStartTime.put(fullMethodName, time);
    }

    public static void setEndTime(String fullMethodName, long time) {
        sEndTime.put(fullMethodName, time);
    }

    public static String getCostTime(String fullMethodName) {
        long start = sStartTime.get(fullMethodName);
        long end = sEndTime.get(fullMethodName);
        return "method: " + fullMethodName + " -cost- " + Long.valueOf(end - start) + " ns";
    }

}
