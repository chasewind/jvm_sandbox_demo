package com.deer.agent;


import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.listener.EventListener;
import com.alibaba.jvm.sandbox.core.util.Sequencer;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;

public class AliAgentMain {
    // 观察ID序列生成器
    private final Sequencer watchIdSequencer = new Sequencer();

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("premain");
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("load agent...");
        Class[] clazzList = inst.getAllLoadedClasses();
        inst.addTransformer(new SandboxClassFileTransformer(1, new EventListener() {//监听到的事件，不做任何处理
            @Override
            public void onEvent(Event event) throws Throwable {

            }
        }, true, new Event.Type[]{Event.Type.BEFORE},
                "debug"), true);
        Arrays.stream(clazzList).forEach(clazz -> {
            if (clazz.getName().startsWith("com.deer.base.service")) {
                //
                try {
                    inst.retransformClasses(clazz);
                } catch (Exception e) {
                    //
                    e.printStackTrace();

                }
            }
        });

    }
}
