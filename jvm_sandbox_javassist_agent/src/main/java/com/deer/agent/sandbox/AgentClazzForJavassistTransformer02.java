package com.deer.agent.sandbox;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class AgentClazzForJavassistTransformer02 implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {

            if(className!= null && (className.startsWith("com/deer/base/service") || className.startsWith("com/deer/base/controller"))){
                String dotClassName = className.replaceAll("/", "\\.");

                ClassPool cp = ClassPool.getDefault();
                CtClass cc = cp.get(dotClassName);
                CtMethod[] methods = cc.getDeclaredMethods();
                if(cc.isInterface()){
                    return classfileBuffer;
                }
                for (CtMethod method : methods) {
                    String[] clazzNameArray = dotClassName.split("\\.");
                    //
                    method.addLocalVariable("simpleMethodName",cp.get(String.class.getName()));
                    String simpleMethodName = clazzNameArray[clazzNameArray.length-1]+"."+method.getName();
                    method.insertBefore("simpleMethodName=\""+simpleMethodName+"\";com.deer.agent.sandbox.TraceRecorder.before(simpleMethodName);");
                    method.insertAfter("com.deer.agent.sandbox.TraceRecorder.after(simpleMethodName);");
                }
                classfileBuffer = cc.toBytecode();
                cc.detach();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return classfileBuffer;
    }
}
