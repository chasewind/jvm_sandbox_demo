package com.deer.agent.sandbox.matcher;

public interface ClassAndMethodMatcher {


    boolean match(Class<?>clazz,String clazzName,String methodName,String methodDesc);
}
