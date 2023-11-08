package com.deer.agent;

import com.deer.agent.sandbox.AgentClazzTransformer;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;

public class AgentMain {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("premain");
    }
    public static void agentmain(String agentArgs, Instrumentation inst){
        System.out.println("load agent...");
        Class[] clazzList = inst.getAllLoadedClasses();
        boolean isNativeSupported = inst.isNativeMethodPrefixSupported();
        AgentClazzTransformer agentClazzTransformer=  new AgentClazzTransformer(isNativeSupported);
        inst.addTransformer(agentClazzTransformer, true);

        Arrays.stream(clazzList).forEach(clazz->{
            if(clazz.getName().startsWith("com.deer.base")){
                //
                try {
                    inst.retransformClasses(clazz);
                }catch (Exception e){
                    //
                    e.printStackTrace();
                }
            }
        });

    }
}
