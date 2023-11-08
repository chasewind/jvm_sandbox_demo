package com.deer.agent.sandbox.entire;

import org.objectweb.asm.MethodVisitor;
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
    public void onMethodEnter() {
        super.onMethodEnter();
    }

    @Override
    public void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
    }
}
