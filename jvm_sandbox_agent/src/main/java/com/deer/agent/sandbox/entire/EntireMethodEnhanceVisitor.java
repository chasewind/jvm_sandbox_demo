package com.deer.agent.sandbox.entire;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class EntireMethodEnhanceVisitor extends ClassVisitor implements Opcodes {
    private String clazzName;
    public EntireMethodEnhanceVisitor(ClassVisitor classVisitor) {
        super(ASM7, classVisitor);
    }
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        clazzName = name;

    }
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        //跳过构造函数
        if(StringUtils.equals(name,"<init>") || StringUtils.equals(name,"<clinit>")){
            return mv;
        }
        //跳过抽象方法和native方法
        boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
        boolean isNativeMethod = (access & ACC_NATIVE) != 0;
        if (isAbstractMethod || isNativeMethod) {
            return mv;
        }
        //判断是否匹配
        if(!clazzName.startsWith("com/deer/base")){
            return mv;
        }

        mv = new EntireStandardMethodVisitor(ASM7, mv, access, name, descriptor,clazzName);
        return mv;
    }
}
