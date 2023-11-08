package com.deer.agent.sandbox;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class MethodParamRecorderVisitor extends AdviceAdapter {
    boolean methodPass = false;
    private String clazzName;
    private String methodName;
    private String methodDesc;
    private String REQUEST_DESC = "Lorg/springframework/web/bind/annotation/RequestMapping;";

    protected MethodParamRecorderVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor, String clazzName) {
        super(api, methodVisitor, access, name, descriptor);
        this.clazzName = clazzName;
        this.methodName = name;
        this.methodDesc = descriptor;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (StringUtils.equals(descriptor, REQUEST_DESC)) {
            methodPass = true;
        }
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    protected void onMethodEnter() {
        //第一个参数返回结果，暂时没有先放个null
        push((Type) null);
        //记录方法入参
        loadArgArray();
        //记录类名
        push(clazzName);
        //记录方法名
        push(methodName);
        //记录方法描述
        push(methodDesc);
        //调用工具类方法记录
        visitMethodInsn(INVOKESTATIC, "com/deer/agent/sandbox/Sender", "send", "(Ljava/lang/Object;[Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);
        //记录类名，方法名，方法描述准备记录全链路信息

        if(methodPass){
            //记录类名
            push(clazzName);
            //记录方法名
            push(methodName);
            //记录方法描述
            push(methodDesc);
            //调用链路追踪
            visitMethodInsn(INVOKESTATIC, "com/deer/agent/sandbox/trace/TraceRecorder", "before", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);

        }
    super.onMethodEnter();
    }

    @Override
    protected void onMethodExit(int opcode) {
        if(methodPass){
            //记录类名
            push(clazzName);
            //记录方法名
            push(methodName);
            //记录方法描述
            push(methodDesc);
            //调用链路追踪
            visitMethodInsn(INVOKESTATIC, "com/deer/agent/sandbox/trace/TraceRecorder", "after", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);
        }

        super.onMethodExit(opcode);
    }
}
