package com.deer.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
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
//                System.out.println("onDiscovery: " + s);

            }

            @Override
            public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b, DynamicType dynamicType) {
//                System.out.println("onTransformation: " + typeDescription);
            }

            @Override
            public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b) {
//                System.out.println("onIgnored: " + typeDescription);
            }

            @Override
            public void onError(String s, ClassLoader classLoader, JavaModule javaModule, boolean b, Throwable throwable) {
                System.out.println("onError: " + s);
                throwable.printStackTrace();
            }

            @Override
            public void onComplete(String typeName, ClassLoader classLoader, JavaModule javaModule, boolean b) {
                if(typeName.startsWith("com.deer")){
                    System.out.println("onComplete: " + typeName);
                }

            }

        };
        new AgentBuilder.Default()
                .with(AgentBuilder.LambdaInstrumentationStrategy.DISABLED)
                .type(ElementMatchers.nameMatches("com.deer.special.RunnableWrapper"))
                .transform((builder, typeDescription, classLoader, javaModule,protectionDomain) -> {
                            if (typeDescription.getName().equals("com.deer.special.RunnableWrapper")) {
                                builder = builder
                                        .defineField("traceId", String.class, Visibility.PUBLIC)
                                        // 拦截构造函数，设置 traceId
                                        .constructor(ElementMatchers.any())
                                        .intercept(Advice.to(ConstructorInterceptor.class))
                                        .method(ElementMatchers.named("run"))
                                        .intercept(MethodDelegation.to(CommonThreadInterceptor.class));
                                DynamicType.Unloaded<?> dynamicType = builder.make();
                                // 将字节码写入文件
                                writeClassToFile(dynamicType, dir);

                            }
                            return builder;
                        }
                )
                .type(ElementMatchers.nameMatches("com.deer.special.FunctionWrapper"))
                .transform((builder, typeDescription, classLoader, javaModule,protectionDomain) ->
                        builder.method(ElementMatchers.named("apply"))
                                .intercept(MethodDelegation.to(CommonLambdaInterceptor.class)))
                .type(ElementMatchers.nameMatches("com.deer.special.ConsumerWrapper"))
                .transform((builder, typeDescription, classLoader, javaModule,protectionDomain) ->
                        builder.method(ElementMatchers.named("accept"))
                                .intercept(MethodDelegation.to(CommonLambdaInterceptor.class)))
                .type(ElementMatchers.isAnnotatedWith(ElementMatchers.named("org.springframework.web.bind.annotation.RestController")))
                .transform((builder, typeDescription, classLoader, javaModule,protectionDomain) ->
                        builder.method(ElementMatchers.not(ElementMatchers.isConstructor())
                                        .and(ElementMatchers.not(ElementMatchers.named("equals")))
                                        .and(ElementMatchers.not(ElementMatchers.named("toString")))
                                        .and(ElementMatchers.not(ElementMatchers.named("hashCode")))
                                        .and(ElementMatchers.not(ElementMatchers.named("clone")))
                                        .and(ElementMatchers.not(ElementMatchers.named("finalize"))))
                                .intercept(MethodDelegation.to(SpringControllerInterceptor.class)))
                .type(ElementMatchers.nameStartsWith("com.deer.base.service"))
                .transform((builder, typeDescription, classLoader, javaModule,protectionDomain) ->{
                    builder= builder.method(ElementMatchers.not(ElementMatchers.isConstructor())
                            .and(ElementMatchers.not(ElementMatchers.named("equals")))
                            .and(ElementMatchers.not(ElementMatchers.named("toString")))
                            .and(ElementMatchers.not(ElementMatchers.named("hashCode")))
                            .and(ElementMatchers.not(ElementMatchers.named("clone")))
                            .and(ElementMatchers.not(ElementMatchers.named("finalize"))))
                            .intercept(MethodDelegation.to(CommonBizInterceptor.class));
                    DynamicType.Unloaded<?> dynamicType = builder.make();
                    // 将字节码写入文件
                    writeClassToFile(dynamicType, dir);
                 return builder;
                })
                .with(listener)
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
