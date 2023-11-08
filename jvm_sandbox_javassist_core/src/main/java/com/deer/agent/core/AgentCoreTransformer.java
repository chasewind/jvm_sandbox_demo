package com.deer.agent.core;

import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class AgentCoreTransformer implements ClassFileTransformer {

    /**传递类管理权限，否则找不到类报错javassist.CannotCompileException: [source error] no such class: com.deer.agent.core.TraceRecorder*/
    private String passClassPath ;
    public AgentCoreTransformer(String passClassPath){
        this.passClassPath = passClassPath;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {

            if(className!= null  ){
                String dotClassName = className.replaceAll("/", "\\.");
                if(dotClassName.startsWith("com.sun.proxy.$Proxy")){
                    return classfileBuffer;
                }
                ClassPool cp = ClassPool.getDefault();
                //特别重要
                cp.appendClassPath(passClassPath);
                CtClass cc = cp.get(dotClassName);
                CtMethod[] methods = cc.getDeclaredMethods();
                if(cc.isInterface()){
                    return classfileBuffer;
                }
                if(cc.isFrozen()){
                    return classfileBuffer;
                }
                if(cc.isEnum()){
                    return classfileBuffer;
                }
                if(cc.isAnnotation()){
                    return classfileBuffer;
                }
                if (className.startsWith("sun/")
                        || className.startsWith("com/sun/")
                        || className.startsWith("java/")
                        || className.startsWith("javax/")
                        || className.startsWith("jdk/")
                          ||className.startsWith("org/springframework/ui")
                        ||className.startsWith("org/springframework/util")
                        ||className.startsWith("org/springframework/context")
                        ||className.startsWith("org/springframework/core")
                        //
                        ||className.startsWith("org/springframework/web")
                        ||className.startsWith("org/springframework/http")
                        //
                        || className.startsWith("javassist/")
                        || className.startsWith("org/apache/catalina")
                        || className.startsWith("org/apache/tomcat")
                        || className.startsWith("org/apache/coyote")
                        || className.startsWith("org/apache/juli")
                        || className.startsWith("org/apache/naming")
                        || className.startsWith("com/fasterxml")
                        || className.startsWith("org/apache/commons/lang3")

                ) {
                    return classfileBuffer;
                }
                if(className.startsWith("com/deer/agent")){
                    return classfileBuffer;
                }

                for (CtMethod method : methods) {
                    //
                    if(isNative(method) ||isAbstract(method)){
                        continue;
                    }
                    //
                     if( "toString".equals(method.getName())
                            || "getClass".equals(method.getName())
                            || "equals".equals(method.getName())
                            || "hashCode".equals(method.getName())){
                         continue;
                     }
                    String[] clazzNameArray = dotClassName.split("\\.");
                    //
                    method.addLocalVariable("simpleMethodName",cp.get(String.class.getName()));
                    //String simpleMethodName = clazzNameArray[clazzNameArray.length-1]+"."+method.getName();
                    String simpleMethodName=dotClassName+"."+method.getName();
                    method.insertBefore("simpleMethodName=\""+simpleMethodName+"\";com.deer.agent.core.TraceRecorder.before(simpleMethodName);");
                    method.insertAfter("com.deer.agent.core.TraceRecorder.after(simpleMethodName);");
                }
                classfileBuffer = cc.toBytecode();
                cc.detach();

            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return classfileBuffer;
    }
    public static boolean isNative(CtMethod method) {
        return Modifier.isNative(method.getModifiers());
    }
    public static boolean isAbstract(CtBehavior method) {
        return Modifier.isAbstract(method.getModifiers());
    }
}
