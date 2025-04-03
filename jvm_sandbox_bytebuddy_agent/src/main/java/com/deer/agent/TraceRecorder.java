package com.deer.agent;

import java.util.List;
import java.util.Map;

public class TraceRecorder {

    private  static final ThreadLocal<Trace> cache = new ThreadLocal<Trace>() {
        @Override
        protected Trace initialValue() {
            return new Trace();
        }
    };


    public static void recordEntranceTrace(String className, String methodName) {
        Trace trace = cache.get();
        trace.before();
        trace.record(className,methodName);
    }

    public static void recordProcessTrace(String className, String methodName) {
        Trace trace = cache.get();
        trace.before();
        trace.record(className,methodName);
    }

    public static void recordExitTrace(String className, String methodName) {
        Trace trace = cache.get();
        trace.after();
        trace.record(className,methodName);
        //打印树形结构并退出
        Map<Integer, List<String>> map =  trace.map;
        //按key从小到达顺序遍历
        System.out.println("------------------start-----------------------");
        map.keySet().stream()
                .sorted()
                .forEach(key -> {
                  List<String>list =  map.get(key);
                  list.forEach(e->{
                      System.out.println(getIndent(key)+"-->"+e);
                  });
                });
        Map<Integer,String> routeMap = trace.routeMap;
        routeMap.keySet().stream()
                .sorted()
                .forEach(key->{
                    System.out.println(getIndent(key)+routeMap.get(key));
                });
                System.out.println("------------------end-----------------------");
        cache.remove();
    }

    public static void recordExitProcessTrace(String className, String methodName) {
        Trace trace = cache.get();
        trace.after();
        trace.record(className,methodName);
    }
    private static String getIndent(int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("--"); // 每个层级两个短划线
        }
        return indent.toString();
    }
}
