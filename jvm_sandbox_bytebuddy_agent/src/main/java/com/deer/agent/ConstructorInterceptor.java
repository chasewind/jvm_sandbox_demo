package com.deer.agent;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bind.annotation.This;

import java.util.UUID;

public class ConstructorInterceptor {

    @Advice.OnMethodEnter
    public static void onEnter(@This Object obj, @Advice.AllArguments Object[] args) {
        // 设置 traceId 字段
        String linkId = TrackManager.getCurrentSpan();
        if (null == linkId) {
            linkId = UUID.randomUUID().toString();
            TrackContext.setLinkId(linkId);
        }
        System.out.println("enter traceId: " + linkId);

    }
    @Advice.OnMethodExit
    public static void onExit(@Advice.This Object obj,@Advice.Origin String methodName) {
        String linkId = TrackManager.getCurrentSpan();
        System.out.println("again traceId: "+linkId);
        java.lang.reflect.Field field = null;
        try {
            field = obj.getClass().getDeclaredField("traceId");
            if(field != null){
                field.setAccessible(true);
                field.set(obj, linkId);
            }
        } catch (Exception e) {
           e.printStackTrace();
        }

    }

}
