package com.deer.agent.sandbox;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class MethodResultRecorderVisitor extends AdviceAdapter {
    String methodId;
    private String clazzName;
    private String methodName;
    private String methodDesc;

    protected MethodResultRecorderVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor, String clazzName, String methodId) {
        super(api, methodVisitor, access, name, descriptor);
        this.clazzName = clazzName;
        this.methodName = name;
        this.methodDesc = descriptor;
        this.methodId = methodId;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {

        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    protected void onMethodEnter() {


        super.onMethodEnter();
    }

    @Override
    protected void onMethodExit(int opcode) {
        //第一个参数返回结果
        loadReturn(opcode);

        push(methodId);
        //记录方法入参
        loadArgArray();
        //记录类名
        push(clazzName);
        //记录方法名
        push(methodName);
        //记录方法描述
        push(methodDesc);
        //调用工具类方法记录
        visitMethodInsn(INVOKESTATIC, "com/deer/agent/sandbox/Sender", "send", "(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/deer/agent/sandbox/Ret;", false);
        //记录类名，方法名，方法描述准备记录全链路信息

        //复制一份，调用getField拿到结果中的status字段
        dup();
        visitFieldInsn(GETFIELD,Type.getType(Ret.class).getInternalName(),"status",Type.BOOLEAN_TYPE.getDescriptor());
        //判断status的值是否和布尔值true相等
        dup();
        Label returnLabel = new Label();
        Label finishLabel = new Label();
        visitJumpInsn(IFEQ, returnLabel);
        goTo(finishLabel);


        mark(returnLabel);
        pop();
        visitFieldInsn(GETFIELD,Type.getType(Ret.class).getInternalName(),"result",Type.getType(Object.class).getDescriptor());
        checkCastReturn(Type.getReturnType(methodDesc));

        mark(finishLabel);
        pop();
        visitFieldInsn(GETFIELD,Type.getType(Ret.class).getInternalName(),"response",Type.getType(Object.class).getDescriptor());
        checkCastReturn(Type.getReturnType(methodDesc));

    }

    final protected void checkCastReturn(Type returnType) {
        final int sort = returnType.getSort();
        switch (sort) {
            case Type.VOID: {
                pop();
                mv.visitInsn(Opcodes.RETURN);
                break;
            }
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT: {
                unbox(returnType);
                returnValue();
                break;
            }
            case Type.FLOAT: {
                unbox(returnType);
                mv.visitInsn(Opcodes.FRETURN);
                break;
            }
            case Type.LONG: {
                unbox(returnType);
                mv.visitInsn(Opcodes.LRETURN);
                break;
            }
            case Type.DOUBLE: {
                unbox(returnType);
                mv.visitInsn(Opcodes.DRETURN);
                break;
            }
            case Type.ARRAY:
            case Type.OBJECT:
            case Type.METHOD:
            default: {
                unbox(returnType);
                mv.visitInsn(ARETURN);
                break;
            }

        }
    }

    /**
     * 加载返回值
     *
     * @param opcode 操作码
     */
    private void loadReturn(int opcode) {
        switch (opcode) {

            case RETURN: {
                push((Type) null);
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
}
