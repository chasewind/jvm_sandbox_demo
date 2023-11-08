package com.deer.agent.sandbox;

public class PrintUtils {

    public static void printException(String methodName, Exception exception) {
        System.out.println("监控 -> [方法名：" + methodName + "，异常信息：" + exception.getMessage() + "]");
    }

}

