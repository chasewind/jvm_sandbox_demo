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
import java.lang.instrument.Instrumentation;

public class AgentMainForByteBuddy {

    public static void premain(String featureString, Instrumentation inst) {
        setupAgent(inst);
    }
    public static void agentmain(String agentArgs, Instrumentation inst){
        setupAgent(inst);

    }
//    // 假设你想重新转换某个包内的所有类，例如 com.deer.base.controller
//    String packageName = "com.deer.base.controller";
//    retransformClassesInPackage(packageName, inst);
    public static void retransformClassesInPackage(String packageName, Instrumentation inst) {
        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            if (clazz.getName().startsWith(packageName)) {
                retransformClass(clazz, inst);
            }
        }
    }
    public static void retransformClass(Class<?> clazz, Instrumentation inst) {
        try {
            inst.retransformClasses(clazz);
            System.out.println("成功重新转换类: " + clazz.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setupAgent(Instrumentation inst) {
        // 指定输出目录
        String outputDir = "./output/";
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
//        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule) -> {
////            return builder.method(ElementMatchers.any()).intercept(MethodDelegation.to(MethodCostTime.class));
//
//            //Advice
////            if (classLoader != null && typeDescription.getName().startsWith("com.deer.base.service")) {
////                DynamicType.Unloaded<?> dynamicType = builder.method(ElementMatchers.any()).intercept(Advice.to(MethodInterceptor.class)).make();
////                // 将字节码写入文件
////                writeClassToFile(dynamicType, dir);
////                return builder.method(ElementMatchers.any()).intercept(Advice.to(MethodInterceptor.class));
////            }
//            //MethodDelegation
//            if (classLoader != null && typeDescription.getName().startsWith("com.deer.base")) {
////                DynamicType.Unloaded<?> dynamicType = builder.method(
////                        ElementMatchers.not(ElementMatchers.isConstructor())
////                                .and(ElementMatchers.not(ElementMatchers.named("equals")))
////                                .and(ElementMatchers.not(ElementMatchers.named("toString")))
////                                .and(ElementMatchers.not(ElementMatchers.named("hashCode")))
////                                .and(ElementMatchers.not(ElementMatchers.named("clone")))
////                                .and(ElementMatchers.not(ElementMatchers.named("finalize")))
////                ).intercept(MethodDelegation.to(MethodAroundInterceptor.class)).make();
////                // 将字节码写入文件
////                writeClassToFile(dynamicType, dir);
//                return builder.method(
//                        ElementMatchers.not(ElementMatchers.isConstructor())
//                                .and(ElementMatchers.not(ElementMatchers.named("equals")))
//                                .and(ElementMatchers.not(ElementMatchers.named("toString")))
//                                .and(ElementMatchers.not(ElementMatchers.named("hashCode")))
//                                .and(ElementMatchers.not(ElementMatchers.named("clone")))
//                                .and(ElementMatchers.not(ElementMatchers.named("finalize")))
//                ).intercept(MethodDelegation.to(MethodAroundInterceptor.class));
//            }
//            //
//
//            return builder; // 不进行转换
//        };


        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule) -> {
            //MethodDelegation
            if (classLoader != null ) {
                builder = builder.method(
                        ElementMatchers.not(ElementMatchers.isConstructor())
                                .and(ElementMatchers.not(ElementMatchers.named("equals")))
                                .and(ElementMatchers.not(ElementMatchers.named("toString")))
                                .and(ElementMatchers.not(ElementMatchers.named("hashCode")))
                                .and(ElementMatchers.not(ElementMatchers.named("clone")))
                                .and(ElementMatchers.not(ElementMatchers.named("finalize")))
                                .or(ElementMatchers.named("run"))
                                .or(ElementMatchers.named("apply"))
                                .or(ElementMatchers.named("accept"))
                ).intercept(MethodDelegation.to(MethodTraceInterceptor.class));
                //
                DynamicType.Unloaded<?> dynamicType =builder.make();
                // 将字节码写入文件
                writeClassToFile(dynamicType, dir);
            }

            return builder; // 不进行转换
        };


//        AgentBuilder.Transformer httpTransformer = (builder, typeDescription, classLoader, javaModule) -> {
//            return builder
//                    .method(ElementMatchers.any())
//                    .intercept(Advice.to(HttpLogInterceptor.class));
//        };
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

//        new AgentBuilder
//                .Default()
//                .type(ElementMatchers.nameStartsWith("com.deer.base"))
//                .transform(transformer)
//                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
////                .with(AgentBuilder.LambdaInstrumentationStrategy.ENABLED)
//                .with(listener)
//                .installOn(inst);
////        new AgentBuilder.Default()
////                .type(ElementMatchers.nameEndsWith("Controller"))
////                .transform(httpTransformer).installOn(inst);

        //丰富条件
        new AgentBuilder
                .Default()
                .type(
                        ElementMatchers.nameMatches("com.deer.special.RunnableWrapper")
                                .or(ElementMatchers.nameMatches("com.deer.special.FunctionWrapper"))
                                .or(ElementMatchers.nameMatches("com.deer.special.ConsumerWrapper"))
                                .or(ElementMatchers.nameStartsWith("com.deer.base.service"))
                                .or(ElementMatchers.nameStartsWith("com.deer.base.controller"))
                )
                .transform(transformer)
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
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
