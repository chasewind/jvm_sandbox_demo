package com.deer.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;

public class AgentMainForByteBuddy {

    public static void premain(String featureString, Instrumentation inst) {
        setupAgent(inst);
    }
    public static void agentmain(String agentArgs, Instrumentation inst){
        setupAgent(inst);

    }


    private static void setupAgent(Instrumentation inst) {
        String outputDir = "./output/";
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        new AgentBuilder.Default()
                .type(ElementMatchers.nameMatches("com.deer.special.RunnableWrapper"))
                .transform((builder, typeDescription, classLoader, javaModule) ->
                     builder.method(ElementMatchers.named("run"))
                                .intercept(MethodDelegation.to(CommonThreadInterceptor.class)))
                .type(ElementMatchers.nameMatches("com.deer.special.FunctionWrapper"))
                .transform((builder, typeDescription, classLoader, javaModule) ->
                        builder.method(ElementMatchers.named("apply"))
                                .intercept(MethodDelegation.to(CommonLambdaInterceptor.class)))
                .type(ElementMatchers.nameMatches("com.deer.special.ConsumerWrapper"))
                .transform((builder, typeDescription, classLoader, javaModule) ->
                        builder.method(ElementMatchers.named("accept"))
                                .intercept(MethodDelegation.to(CommonLambdaInterceptor.class)))
                .installOn(inst);


    }

    private static void writeClassToFile(DynamicType.Unloaded<?> dynamicType, File dir) {
        String className = dynamicType.getTypeDescription().getName();
        String fileName = className.replace('.', '/') + ".class";
        File outputFile = new File(dir, fileName);
        // 创建父目录
        File parentDir = outputFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs(); // 确保父目录存在
        }
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(dynamicType.getBytes());
            System.out.println("Class written to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
