package com.deer.agent.sandbox;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.concurrent.atomic.AtomicInteger;

public class TraceMethodClassVisitor extends ClassVisitor implements Opcodes {
    private String clazzName;

    private String CONTROLLER_DESC="Lorg.springframework.stereotype.Controller;";
    private String REST_CONTROLLER_DESC="Lorg/springframework/web/bind/annotation/RestController;";
    private String REQUEST_DESC="Lorg/springframework/web/bind/annotation/RequestMapping;";

     boolean controllerAdvice = false;
    private static final AtomicInteger sequence = new AtomicInteger(1000);
    public TraceMethodClassVisitor(ClassVisitor classVisitor) {
        super(ASM7, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        clazzName = name;

    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        //拦截Controller 和RestController类注解
        if(StringUtils.equals(descriptor,CONTROLLER_DESC) || StringUtils.equals(descriptor,REST_CONTROLLER_DESC) || StringUtils.equals(descriptor,REQUEST_DESC)){
            controllerAdvice=true;
        }
        if(clazzName.startsWith("com/deer/base")){
            controllerAdvice=true;
        }
        return super.visitAnnotation(descriptor, visible);
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
        if (!controllerAdvice) {
            return mv;
        }
        if(!clazzName.startsWith("com/deer/base")){
            return mv;
        }
        System.out.println("enter..."+clazzName);
        String  methodId = sequence.incrementAndGet()+"--1";
        mv = new MethodResultRecorderVisitor(ASM7, mv, access, name, descriptor,clazzName,methodId);
        return mv;
    }
}
