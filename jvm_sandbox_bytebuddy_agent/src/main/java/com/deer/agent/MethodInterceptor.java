package com.deer.agent;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Parameter;
import java.lang.reflect.Method;
import java.util.Stack;
public class MethodInterceptor {
    public static final ThreadLocal<Stack<String>> callStack = ThreadLocal.withInitial(Stack::new);
    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method,  @Advice.AllArguments Object[] args ) {
        System.out.println("in agent:"+MethodInterceptor.class.getClassLoader());
        Thread currentThread = Thread.currentThread();
        System.out.println("agent is running in thread: " + currentThread.getName() + " (ID: " + currentThread.getId() + ")");
        // 获取参数名
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            String paramName = parameters[i].getName(); // 获取参数名
            Object paramValue = args[i]; // 获取参数值
            System.out.println("Parameter name: " + paramName + ", value: " + paramValue);
        }
        Stack<String> stack = callStack.get();
        stack.push(method.toGenericString());
        System.out.println("Entering method: " + method);
        printCallStack();
    }
    @Advice.OnMethodExit
    public static void onExit(@Advice.Origin Method method  ) {
        Stack<String> stack = callStack.get();
        if (!stack.isEmpty()) {
            stack.pop();
        }
        System.out.println("Exiting method: " + method.toGenericString());

        printCallStack();
    }

    //下述方法是Advice注解的弊端，返回值处理很乏力
//    @Advice.OnMethodExit
//    public static void onExit(@Advice.Origin Method method, @Advice.Return(readOnly = false) Object returnValue ) {
//        Stack<String> stack = callStack.get();
//        if (!stack.isEmpty()) {
//            stack.pop();
//        }
//        System.out.println("Exiting method: " + method.toGenericString());
//        // 检查方法是否为 void
//        if (!method.getReturnType().equals(void.class)) {
//            // 处理非 void 返回值
//            if (returnValue != null) {
//                if (returnValue instanceof String) {
//                    String result = (String) returnValue; // 如果返回值是 String 类型，可以进行转换
//                    System.out.println("Method execution finished!");
//                    System.out.println("Return value: " + result);
//                } else {
//                    System.out.println("Method execution finished with non-string return value!");
//                }
//            } else {
//                System.out.println("Method execution finished with no return value!");
//            }
//        } else {
//            System.out.println("Method execution finished (void method)!");
//        }
//        printCallStack();
//    }
    public static void printCallStack() {
        Stack<String> stack = callStack.get();
        System.out.println("Current call stack: " + stack);
    }
}