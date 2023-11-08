package com.deer.agent;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.listener.EventListener;
import com.alibaba.jvm.sandbox.core.enhance.EventEnhancer;
import com.alibaba.jvm.sandbox.core.util.ObjectIDs;
import com.alibaba.jvm.sandbox.core.util.SandboxClassUtils;
import com.alibaba.jvm.sandbox.core.util.SandboxProtector;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class SandboxClassFileTransformer implements ClassFileTransformer {

    private final int watchId;
    private final EventListener eventListener;
    private final boolean isEnableUnsafe;
    private final Event.Type[] eventTypeArray;

    private final String namespace;
    private final int listenerId;

    SandboxClassFileTransformer(final int watchId,
                                final EventListener eventListener,
                                final boolean isEnableUnsafe,
                                final Event.Type[] eventTypeArray,
                                final String namespace) {
        this.watchId = watchId;
        this.eventListener = eventListener;
        this.isEnableUnsafe = isEnableUnsafe;
        this.eventTypeArray = eventTypeArray;
        this.namespace = namespace;
        this.listenerId = ObjectIDs.instance.identity(eventListener);
    }


    @Override
    public byte[] transform(final ClassLoader loader,
                            final String internalClassName,
                            final Class<?> classBeingRedefined,
                            final ProtectionDomain protectionDomain,
                            final byte[] srcByteCodeArray) {

        SandboxProtector.instance.enterProtecting();
        try {

            // 这里过滤掉Sandbox所需要的类|来自SandboxClassLoader所加载的类|来自ModuleJarClassLoader加载的类
            // 防止ClassCircularityError的发生
            if (SandboxClassUtils.isComeFromSandboxFamily(internalClassName, loader)) {
                return null;
            }
            if(!internalClassName.startsWith("com/deer/base")){
                return srcByteCodeArray;
            }else{
                System.out.println("---"+internalClassName);
            }
            return _transform(
                    loader,
                    internalClassName,
                    classBeingRedefined,
                    srcByteCodeArray
            );


        } catch (Throwable cause) {
           cause.printStackTrace();
            return null;
        } finally {
            SandboxProtector.instance.exitProtecting();
        }
    }

    private byte[] _transform(final ClassLoader loader,
                              final String internalClassName,
                              final Class<?> classBeingRedefined,
                              final byte[] srcByteCodeArray) {
        // 如果未开启unsafe开关，是不允许增强来自BootStrapClassLoader的类
        if (!isEnableUnsafe
                && null == loader) {
            System.out.println("transform ignore "+internalClassName+", class from bootstrap but unsafe.enable=false.");
            return null;
        }
        System.out.println("eventTypeArray---"+eventTypeArray.length);

        // 开始进行类匹配
        try {
            final byte[] toByteCodeArray = new EventEnhancer().toByteCodeArray(
                    loader,
                    srcByteCodeArray,
                    namespace,
                    listenerId,
                    eventTypeArray
            );
            if (srcByteCodeArray == toByteCodeArray) {
                System.out.println("transform ignore "+internalClassName+", nothing changed in loader="+loader);
                return null;
            }

            System.out.println("transform "+internalClassName+" finished, in loader="+loader);
            return toByteCodeArray;
        } catch (Throwable cause) {
            System.out.println("transform "+internalClassName+" failed, in loader="+loader);
            cause.printStackTrace();
            return null;
        }
    }


    /**
     * 获取观察ID
     *
     * @return 观察ID
     */
    int getWatchId() {
        return watchId;
    }

    /**
     * 获取事件监听器
     *
     * @return 事件监听器
     */
    EventListener getEventListener() {
        return eventListener;
    }

    /**
     * 获取事件监听器ID
     *
     * @return 事件监听器ID
     */
    int getListenerId() {
        return listenerId;
    }


    /**
     * 获取本次监听事件类型数组
     *
     * @return 本次监听事件类型数组
     */
    Event.Type[] getEventTypeArray() {
        return eventTypeArray;
    }



}
