package com.deer.trace.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;

public class AgentMainForByteBuddy {

    public static void premain(String featureString, Instrumentation inst) {
        setupAgent(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        setupAgent(inst);

    }


    private static void setupAgent(Instrumentation inst) {
        String outputDir = "./output/";
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        AgentBuilder.Listener listener = new AgentBuilder.Listener() {
            @Override
            public void onDiscovery(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {

            }

            @Override
            public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b, DynamicType dynamicType) {
            }

            @Override
            public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b) {
            }

            @Override
            public void onError(String s, ClassLoader classLoader, JavaModule javaModule, boolean b, Throwable throwable) {
                System.out.println("onError: " + s);
            }

            @Override
            public void onComplete(String typeName, ClassLoader classLoader, JavaModule javaModule, boolean b) {
            }

        };
        new AgentBuilder.Default()
                .with(AgentBuilder.LambdaInstrumentationStrategy.DISABLED)
                .with(listener)
                .installOn(inst);


    }

}
