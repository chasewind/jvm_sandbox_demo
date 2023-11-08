package com.deer.agent.sandbox;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class AgentClazzForJavassistTransformer01 implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {

            if(className!= null && className.startsWith("com/deer/base/service")){
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
                    method.addLocalVariable("startTime",CtClass.longType);
                    method.insertBefore("startTime = System.nanoTime();");
                    StringBuilder endBlock = new StringBuilder();

                    method.addLocalVariable("endTime", CtClass.longType);
                    method.addLocalVariable("opTime", CtClass.longType);
                    endBlock.append(
                            "endTime = System.nanoTime();");
                    endBlock.append(
                            "opTime = endTime-startTime;");

                    endBlock.append(
                            "System.out.println(\"method "+clazzNameArray[clazzNameArray.length-1]+"."+method.getName()+" cost :" +
                                    "\" + opTime + \" ns!\");");

                    method.insertAfter(endBlock.toString());

                    classfileBuffer = cc.toBytecode();
                    cc.detach();
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return classfileBuffer;
    }
}
