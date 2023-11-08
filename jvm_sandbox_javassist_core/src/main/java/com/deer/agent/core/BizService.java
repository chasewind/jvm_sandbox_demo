package com.deer.agent.core;

public class BizService {

    public static int add(int a ,int b){
        TraceRecorder.before("aa","bb","cc");
        return a+b;
    }



}
