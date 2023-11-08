package com.deer.agent.sandbox;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataCache {

    private static Map<String,Ret>cache = new ConcurrentHashMap<>();

    public static void put(String methodId,Ret contextData){
        cache.put(methodId,contextData);
        System.out.println("all keys:"+Arrays.toString(cache.keySet().toArray(new String[0])));
        System.out.println("current:"+contextData);
    }
    public static Ret get(String methodId){
        return cache.get(methodId);
    }
}
