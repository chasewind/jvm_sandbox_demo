package com.deer.agent.sandbox;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class MethodTryCatchVisitor extends AdviceAdapter {

    private Label startLabel = new Label();
    private Label endLabel = new Label();
    private Label handlerLabel = new Label();
    private String clazzName;

    public MethodTryCatchVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor,String clazzName) {
        super(api, methodVisitor, access, name, descriptor);
        this.clazzName=clazzName;
    }

    @Override
    public void onMethodEnter() {
        //内部调用 visitLabel(label) 标记try开始位置
        mark(startLabel);
        //该方法也可以在visitMaxs()写，两个位置没有约束
        visitTryCatchBlock(startLabel, endLabel, handlerLabel, "java/lang/Exception");

        super.onMethodEnter();
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        mark(endLabel);
        mark(handlerLabel);
        visitVarInsn(ASTORE, 1);
        visitTypeInsn(NEW, "java/lang/RuntimeException");
        visitInsn(DUP);
        visitVarInsn(ALOAD, 1);
        visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/Throwable;)V", false);
        visitInsn(ATHROW);
        super.visitMaxs(maxStack, maxLocals);
    }
}
