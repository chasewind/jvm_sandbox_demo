package com.deer.agent;

import net.bytebuddy.asm.Advice;

import java.util.UUID;

public class ConstructorInterceptor {
    @Advice.OnMethodEnter
    public static void onEnter(@Advice.AllArguments Object[] args) {
         Object obj = args[0]; // 假设构造函数有参数
        System.out.println("Entering constructor of: " + obj.getClass().getName());

        // 设置 traceId 字段
        String linkId = TrackManager.getCurrentSpan();
        if (null == linkId) {
            linkId = UUID.randomUUID().toString();
            TrackContext.setLinkId(linkId);
        }
        try {
            if(obj.getClass().getCanonicalName().equals("com.deer.special.RunnableWrapper")){
                // 获取 traceId 字段并设置值
                java.lang.reflect.Field field = obj.getClass().getDeclaredField("traceId");
                if(field != null){
                    field.setAccessible(true); // 设置可访问性
                    field.set(obj, linkId); // 设置字段值
                }
            }


        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace(); // 处理异常
        }
    }
}
