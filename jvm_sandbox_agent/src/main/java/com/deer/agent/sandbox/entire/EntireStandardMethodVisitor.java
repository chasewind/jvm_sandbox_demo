package com.deer.agent.sandbox.entire;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class EntireStandardMethodVisitor extends AdviceAdapter {
    private String clazzName;
    private String methodName;

    public EntireStandardMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor, String clazzName) {
        super(api, methodVisitor, access, name, descriptor);
        this.clazzName = clazzName;
        this.methodName = name;
    }

    @Override
    public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
    }

    @Override
    public void onMethodEnter() {
        loadArgArray();
        push("trace_001");
        //数组占堆栈长度为1，push的字符串在堆栈占用长度也是1，因此这里可以直接同长度swap
        swap();
        push(clazzName);
        push(methodName);
        push(methodDesc);
        invokeStatic(EntireMethodHelper.SimpleRecorder,EntireMethodHelper.SimpleBeforeMethod);
        pop();
        super.onMethodEnter();
    }

    @Override
    public void onMethodExit(int opcode) {
        if(opcode == ATHROW){
            super.onMethodExit(opcode);
            return;
        }
        //这里如果先push，返回结果在堆栈中占据的长度可能是一个，也可能是两个，做复杂的判断和swap就没有意义
        loadReturn(opcode);
        push("trace_001");
        loadArgArray();
        push(clazzName);
        push(methodName);
        push(methodDesc);
        invokeStatic(EntireMethodHelper.SimpleRecorder,EntireMethodHelper.SimpleAfterMethod);
        pop();
        super.onMethodExit(opcode);
    }
    private void loadReturn(int opcode) {
        switch (opcode) {

            case RETURN: {
                pushNull();
                break;
            }

            case ARETURN: {
                dup();
                break;
            }

            case LRETURN:
            case DRETURN: {
                dup2();
                box(Type.getReturnType(methodDesc));
                break;
            }

            default: {
                dup();
                box(Type.getReturnType(methodDesc));
                break;
            }

        }
    }
    final protected void pushNull() {
        push((Type) null);
    }
}
