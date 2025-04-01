package com.deer.agent;

import net.bytebuddy.asm.Advice;
import java.util.Stack;
public class MethodInterceptor {
    public static final ThreadLocal<Stack<String>> callStack = ThreadLocal.withInitial(Stack::new);
    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin String method) {
        Stack<String> stack = callStack.get();
        stack.push(method);
        System.out.println("Entering method: " + method);
        printCallStack();
    }
    @Advice.OnMethodExit
    public static void onExit(@Advice.Origin String method) {
        Stack<String> stack = callStack.get();
        if (!stack.isEmpty()) {
            stack.pop();
        }
        System.out.println("Exiting method: " + method);
        printCallStack();
    }
    public static void printCallStack() {
        Stack<String> stack = callStack.get();
        System.out.println("Current call stack: " + stack);
    }
}