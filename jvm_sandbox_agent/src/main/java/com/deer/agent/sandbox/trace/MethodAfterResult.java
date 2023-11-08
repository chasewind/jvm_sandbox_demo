package com.deer.agent.sandbox.trace;

public class MethodAfterResult {
    /**
     *是否需要继续，如果为true，不干扰原有逻辑，继续执行，反之返回response
     */
    public  boolean needContinue=true;
    public Object response;
}
