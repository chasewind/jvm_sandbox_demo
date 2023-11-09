package com.deer.agent.sandbox.matcher;

public class HttpClientMatcher implements ClassAndMethodMatcher{
    @Override
    public boolean match(Class<?>clazz,String clazzName, String methodName, String methodDesc) {
        return false;
    }
}
