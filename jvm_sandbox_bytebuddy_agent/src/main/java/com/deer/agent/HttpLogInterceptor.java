package com.deer.agent;

import net.bytebuddy.asm.Advice;

public class HttpLogInterceptor {


    @Advice.OnMethodEnter
    public static long onEnter(@Advice.Origin String methodName, @Advice.AllArguments Object[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println("http: enter:"+methodName);
        return startTime;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void onExit(@Advice.Origin String methodName,@Advice.Enter long startTime, @Advice.Return Object obj,@Advice.Thrown Throwable throwable) {
        System.out.println("http: exit:"+methodName);
    }

}

