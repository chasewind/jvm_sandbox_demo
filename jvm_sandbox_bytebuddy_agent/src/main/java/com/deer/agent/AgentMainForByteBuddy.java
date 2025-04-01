package com.deer.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

public class AgentMainForByteBuddy {
    public static void agentmain(String agentArgs, Instrumentation inst){
        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule) -> {
//            return builder.method(ElementMatchers.any()).intercept(MethodDelegation.to(MethodCostTime.class));
//            return builder.method(ElementMatchers.any()).intercept(Advice.to(MethodInterceptor.class));

            if (classLoader != null && typeDescription.getName().startsWith("com.deer.base.service")) {
                return builder.method(ElementMatchers.any()).intercept(Advice.to(MethodInterceptor.class));
            }
            return builder; // 不进行转换
        };
        AgentBuilder.Listener listener = new AgentBuilder.Listener() {
            @Override
            public void onDiscovery(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {
                System.out.println("onDiscovery: " + s);

            }

            @Override
            public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b, DynamicType dynamicType) {
                System.out.println("onTransformation: " + typeDescription);
            }

            @Override
            public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b) {
                System.out.println("onIgnored: " + typeDescription);
            }

            @Override
            public void onError(String s, ClassLoader classLoader, JavaModule javaModule, boolean b, Throwable throwable) {
                System.out.println("onError: " + s);
                throwable.printStackTrace();
            }

            @Override
            public void onComplete(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {
                System.out.println("onComplete: " + s);
            }
        };
        // 定义一个ClassFileTransformer，用于将修改后的类文件写入指定目录
        ClassFileTransformer classFileTransformer = (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            // 将类文件写入指定目录
            String outputDirectory = "target/transformed-classes";
            File outputDir = new File(outputDirectory);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // 构造目标文件路径
            String targetClassName = className.replace('/', File.separatorChar) + ".class";
            File classFile = new File(outputDir, targetClassName);

            try (FileOutputStream fos = new FileOutputStream(classFile)) {
                fos.write(classfileBuffer);
                System.out.println("Modified class file written to: " + classFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return classfileBuffer;
        };
        new AgentBuilder
                .Default()
                .type(ElementMatchers.nameStartsWith("com.deer.base.service"))
                .transform(transformer)
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(listener)
                .installOn(inst);
        // 注册ClassFileTransformer
//        inst.addTransformer(classFileTransformer);
    }
}
