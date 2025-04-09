package com.deer.agent;

public class CommonThreadInterceptor extends PreciseInterceptor{
    @Override
    public String getDescription() {
        return "拦截线程对应的Runnable Callable";
    }
}
