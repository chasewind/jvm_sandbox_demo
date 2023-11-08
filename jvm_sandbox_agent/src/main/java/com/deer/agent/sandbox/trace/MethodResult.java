package com.deer.agent.sandbox.trace;

public class MethodResult {
    public static final int RET_STATE_NONE = 0;
    public static final int RET_STATE_RETURN = 1;
    public static final int RET_STATE_THROWS = 2;
    public  int state;
    public Object result;
    public Object response;
}
