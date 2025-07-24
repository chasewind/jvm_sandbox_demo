package com.deer;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.asm.Advice;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class SuperAgentApp {
    private static final Map<String, Object> memoryMap = new ConcurrentHashMap<>();
    public static void premain(String featureString, Instrumentation inst) {
        setupAgent(inst);
    }
    public static void agentmain(String agentArgs, Instrumentation inst) {
        setupAgent(inst);
    }

    private static void setupAgent(Instrumentation inst) {
        new AgentBuilder.Default()
                .type(
                        // 拦截自定义包下的类
                        ElementMatchers.nameStartsWith("com.deer")
                                .and(
                                        // 排除特定包下的类
                                        ElementMatchers.not(
                                                ElementMatchers.nameStartsWith("org.springframework")
                                                        .or(ElementMatchers.nameStartsWith("org.slf4j"))
                                        )
                                )
//                        ElementMatchers.any()
                )
                .transform((builder, type, classLoader, module,protectionDomain) -> builder
                        .method(ElementMatchers.any())
                        .intercept(MethodDelegation.to(SuperAgentApp.class)))
                .installOn(inst);

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    List<Map.Entry<String, Object>> heapObjects = new ArrayList<>(memoryMap.entrySet());
                    heapObjects.sort(Comparator.comparingLong(e -> getObjectSize(e.getValue())));
                    List<Map.Entry<String, Object>> topN = new ArrayList<>(heapObjects.subList(0, Math.min(10, heapObjects.size())));
                    topN.forEach(entry -> System.out.println("Class: " + entry.getKey() + ", Size: " + getObjectSize(entry.getValue()) + ", JSON: " + entry.getValue()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static long getObjectSize(Object obj) {
        if (obj instanceof String) {
            return ((String) obj).length() * 2 + 40;
        } else if (obj instanceof Integer) {
            return 16;
        } else if (obj instanceof Double) {
            return 24;
        } else if (obj instanceof Long) {
            return 24;
        } else {
            // 简单估算对象大小
            long size = 8; // 对象头大小
            Class<?> clazz = obj.getClass();
            while (clazz != null) {
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.getType().isPrimitive()) {
                        if (field.getType() == int.class || field.getType() == float.class) {
                            size += 4;
                        } else if (field.getType() == long.class || field.getType() == double.class) {
                            size += 8;
                        } else {
                            size += 1;
                        }
                    } else {
                        size += 8;
                    }
                }
                clazz = clazz.getSuperclass();
            }
            return size;
        }
    }

    @RuntimeType
    public static Object intercept(@Origin Method method, @SuperCall Callable<Object> callable) throws Exception {
        Object result = callable.call();
        // 检查 result 是否为 null
        if (result != null) {

            try {
                // 使用自定义类加载器加载 Jackson 的 ObjectMapper
                Class<?> jacksonMapperClass = loadClassFromAgent("com.fasterxml.jackson.databind.ObjectMapper");
                Object jacksonMapper = jacksonMapperClass.getDeclaredConstructor().newInstance();
                // 禁用 SerializationFeature.FAIL_ON_EMPTY_BEANS 特性
                Class<?> serializationFeatureClass = loadClassFromAgent("com.fasterxml.jackson.databind.SerializationFeature");
                Object failOnEmptyBeans = serializationFeatureClass.getField("FAIL_ON_EMPTY_BEANS").get(null);
                Method configureMethod = jacksonMapperClass.getMethod("configure", serializationFeatureClass, boolean.class);
                configureMethod.invoke(jacksonMapper, failOnEmptyBeans, false);

                Method writeValueAsString = jacksonMapperClass.getMethod("writeValueAsString", Object.class);
                String json = (String) writeValueAsString.invoke(jacksonMapper, result);
                memoryMap.put(result.getClass().getName(), json);
            } catch (Exception e) {
                memoryMap.put(result.getClass().getName(), "JSON conversion failed: " + e.getMessage());
            }

        }
        return result;
    }
    private static Class<?> loadClassFromAgent(String className) throws ClassNotFoundException {
        try {
            // 首先尝试通过当前线程的类加载器加载
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            // 如果当前线程的类加载器找不到该类，则使用系统类加载器
            return Class.forName(className);
        }
    }
    @Advice.OnMethodExit
    public static void onExit(@Advice.This Object thiz, @Advice.Origin Method method) {
        if (method.getName().equals("finalize")) {
            memoryMap.remove(thiz.getClass().getName());
        }
    }
}