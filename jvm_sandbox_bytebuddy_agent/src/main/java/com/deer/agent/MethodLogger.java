package com.deer.agent;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
public class MethodLogger {
    private static int depth = 0; // 记录当前调用层级
    public static void logEntry(Method method, Object[] args) {
        String className = method.getDeclaringClass().getName();
        // 打印当前调用的层级
        String indent = getIndent(depth);
        System.out.println(indent + "Entering method: "+className+"-->" + method.toGenericString());
        depth++; // 增加层级
        // 获取参数名
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            String paramName = parameters[i].getName(); // 获取参数名
            Object paramValue = args[i]; // 获取参数值
            System.out.println(indent + "  Parameter name: " + paramName + ", value: " + paramValue);
        }
    }
    public static void logExit(Method method, Object returnValue, long startTime) {
        depth--; // 减少层级
        String indent = getIndent(depth);
        System.out.println(indent + "Return value: " + returnValue);
        System.out.println(indent + method + " 方法耗时： " + (System.nanoTime() - startTime) / 1000 + " 微秒");
    }
    private static String getIndent(int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("--"); // 每个层级两个短划线
        }
        return indent.toString();
    }
}