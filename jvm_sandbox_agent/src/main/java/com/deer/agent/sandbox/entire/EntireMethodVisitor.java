package com.deer.agent.sandbox.entire;
import com.deer.agent.sandbox.trace.MethodResult;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.ArrayList;

public class EntireMethodVisitor extends AdviceAdapter {
    private final Label beginLabel = new Label();
    private final Label endLabel = new Label();
    private final Label beginCatchBlock = new Label();
    private final Label endCatchBlock = new Label();
    private String clazzName;
    private String methodName;
    private int newLocal = -1;
    public EntireMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor, String clazzName) {
        super(api, methodVisitor, access, name, descriptor);
        this.clazzName=clazzName;
        this.methodName = name;
    }
    @Override
    public void onMethodEnter() {
        mark(beginLabel);
        /*
         * [array]
         */
        loadArgArray();
        /*
         * [array,array]
         */
        dup();
        /*
         * [array,array,clazzName]
         */
        push(clazzName);
        /*
         * [array,array,clazzName,methodName]
         */
        push(methodName);
        /*
         * [array,array,clazzName,methodName,methodDesc]
         */
        push(methodDesc);
        /*
         * [array,methodResult]
         */
        invokeStatic(EntireMethodHelper.Recorder,EntireMethodHelper.BeforeMethod);
        /*
         * [methodResult,array]
         */
        swap();
        Type[] argumentTypeArray = Type.getArgumentTypes(methodDesc);
        /*
         * 还原给初始入参
         */
        storeArgArray(argumentTypeArray);
        /*
         * [methodResult,array]
         */
        pop();

          Label finishLabel = new Label();
          Label returnLabel = new Label();
          Label throwsLabel = new Label();
        /*
         * [methodResult,methodResult]
         */
        dup();
        /*
         * [methodResult,state]
         */
        visitFieldInsn(GETFIELD,EntireMethodHelper.Result.getInternalName(),"state",Type.getType(int.class).getDescriptor());
        /*
         * [methodResult,state,state]
         */
        dup();
        /*
         * [methodResult,state,state,1]
         */
        push(MethodResult.RET_STATE_RETURN);
        /*
         * [methodResult,state]---return
         */
        ifICmp(EQ, returnLabel);
        /*
         * [methodResult,state,2]---throw
         */
        push(MethodResult.RET_STATE_THROWS);
        /*
         * [methodResult]---throw
         */
        ifICmp(EQ, throwsLabel);

        goTo(finishLabel);
        mark(returnLabel);
        /*
         * 来源于 [methodResult,state]---return
         * [methodResult]
         */
        pop();
        Type type = Type.getReturnType(methodDesc);

        /*
         * [methodResult.response]
         */
        visitFieldInsn(GETFIELD, EntireMethodHelper.Result.getInternalName(), "response", Type.getType(Object.class).getDescriptor());
        /*
         *  [ ] 清空，返回
         */
        unboxReturn(type);

        mark(throwsLabel);

        /*
         * 来源于 [methodResult,state,2]---throw [methodResult]---throw
         * [methodResult.response]
         */
        visitFieldInsn(GETFIELD, EntireMethodHelper.Result.getInternalName(), "response", Type.getType(Object.class).getDescriptor());
        /*
         * cast [methodResult.response]---[throwable]
         */
        checkCast(Type.getType(Throwable.class));
        /*
         *  [ ] 清空，返回异常
         */
        throwException();

        mark(finishLabel);
        /*
         * return 和throw都不走，走源代码逻辑，[methodResult]
         * [ ] 清空，走源代码逻辑
         */
        pop();

    }
    @Override
    protected void onMethodExit(int opcode) {
        /*
         * [RealResult,RealResult]
         */
        loadReturn(opcode);
        /*
         * [RealResult,RealResult,array]
         */
        loadArgArray();
        /*
         * [RealResult,RealResult,array,clazzName]
         */
        push(clazzName);
        /*
         * [RealResult,RealResult,array,clazzName,methodName]
         */
        push(methodName);
        /*
         * [RealResult,RealResult,array,clazzName,methodName,methodDesc]
         */
        push(methodDesc);
        /*
         * [RealResult,methodResult]
         */
        invokeStatic(EntireMethodHelper.Recorder,EntireMethodHelper.AfterMethod);
        final Label finishLabel = new Label();
        final Label returnLabel = new Label();
        final Label throwsLabel = new Label();
        /*
         * [RealResult,methodResult,methodResult]
         */
        dup();
        /*
         * [RealResult,methodResult,state]
         */
        visitFieldInsn(GETFIELD, EntireMethodHelper.Result.getInternalName(), "state", Type.getType(int.class).getDescriptor());
        /*
         * [RealResult,methodResult,state,state]
         */
        dup();
        /*
         * [RealResult,methodResult,state,state,1]
         */
        push(MethodResult.RET_STATE_RETURN);
        /*
         * [RealResult,methodResult,state]---return
         */
        ifICmp(EQ, returnLabel);
        /*
         * [RealResult,methodResult,state,2]---throw
         */
        push(MethodResult.RET_STATE_THROWS);
        /*
         * [RealResult,methodResult]---throw
         */
        ifICmp(EQ, throwsLabel);

        goTo(finishLabel);
        mark(returnLabel);
        /*
         * 来源于 [RealResult,methodResult,state]---return
         * [RealResult,methodResult]
         */
        pop();
        Type type = Type.getReturnType(methodDesc);
        /*
         * 消耗掉RealResult,要处理好堆栈上RealResult和methodResult的位置关系
         *  [methodResult]
         */
        popRawRespond(type);
        /*
         * [methodResult.response]
         */
        visitFieldInsn(GETFIELD, EntireMethodHelper.Result.getInternalName(), "response", Type.getType(Object.class).getDescriptor());
        /*
         *  [ ] 清空，返回
         */
        unboxReturn(type);
        mark(throwsLabel);
        /*
         * 来源于 [RealResult,methodResult,state,2]---throw [RealResult,methodResult]---throw
         */
        /*
         * 消耗掉RealResult,要处理好堆栈上RealResult和methodResult的位置关系
         *  [methodResult]
         */
        popRawRespond(type);
        /*
         *  [methodResult.response]
         */
        visitFieldInsn(GETFIELD, EntireMethodHelper.Result.getInternalName(), "response", Type.getType(Object.class).getDescriptor());
        /*
         * cast [methodResult.response]---[throwable]
         */
        checkCast(Type.getType(Throwable.class));
        /*
         *  [ ] 清空，返回异常
         */
        throwException();
        mark(finishLabel);
        /*
         * return 和throw都不走，走源代码逻辑，[RealResult,methodResult]
         * 弹出methodResult，走源代码逻辑
         * [RealResult]
         */
        pop();


    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        mark(endLabel);
        mv.visitLabel(beginCatchBlock);
        visitTryCatchBlock(beginLabel, endLabel, beginCatchBlock, Type.getType(Throwable.class).getInternalName());
        newLocal = newLocal(Type.getType(Throwable.class));
        storeLocal(newLocal);
        loadLocal(newLocal);
        //记录类名
        push(clazzName);
        //记录方法名
        push(methodName);
        //记录方法描述
        push(methodDesc);
        invokeStatic(EntireMethodHelper.Recorder,EntireMethodHelper.ThrowMethod);
        processControl(methodDesc, false);
        loadLocal(newLocal);
        throwException();
        mv.visitLabel(endCatchBlock);
        super.visitMaxs(maxStack, maxLocals);
    }

    // 用于try-catch的重排序
    // 目的是让call的try...catch能在exceptions tables排在前边
    private final ArrayList<AsmTryCatchBlock> asmTryCatchBlocks = new ArrayList<>();

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        asmTryCatchBlocks.add(new AsmTryCatchBlock(start, end, handler, type));
    }


    @Override
    public void visitEnd() {
        for (AsmTryCatchBlock tcb : asmTryCatchBlocks) {
            super.visitTryCatchBlock(tcb.start, tcb.end, tcb.handler, tcb.type);
        }
        super.visitLocalVariable("t", Type.getType(Throwable.class).getDescriptor(), null, beginCatchBlock, endCatchBlock, newLocal);
        super.visitEnd();
    }
    final protected void storeArgArray(   Type[] argumentTypeArray ) {
        for (int i = 0; i < argumentTypeArray.length; i++) {
            dup();
            push(i);
            arrayLoad(Type.getType(Object.class));
            unbox(argumentTypeArray[i]);
            storeArg(i);
        }
    }
    private void unboxReturn(Type returnType) {
        /*
         * [respond]
         */
        final int sort = returnType.getSort();
        switch (sort) {
            case Type.VOID: {
                pop();
                /*
                 * []
                 */
                mv.visitInsn(Opcodes.RETURN);
                break;
            }
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT: {
                unbox(returnType);
                /*
                  [unBox respond]
                 */
                returnValue();
                break;
            }
            case Type.FLOAT: {
                unbox(returnType);
                /*
                  [unBox respond]
                 */
                mv.visitInsn(Opcodes.FRETURN);
                break;
            }
            case Type.LONG: {
                unbox(returnType);
                /*
                 * [unBox respond]
                 */
                mv.visitInsn(Opcodes.LRETURN);
                break;
            }
            case Type.DOUBLE: {
                unbox(returnType);
                /*
                 * [unBox respond]
                 */
                mv.visitInsn(Opcodes.DRETURN);
                break;
            }
            case Type.ARRAY:
            case Type.OBJECT:
            case Type.METHOD:
            default: {
                // checkCast(returnType);
                unbox(returnType);
                /*
                 * [unBox respond]
                 */
                mv.visitInsn(ARETURN);
                break;
            }

        }
    }
    final protected void pushNull() {
        push((Type) null);
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
    private void popRawRespond(Type returnType) {
        final int sort = returnType.getSort();
        switch (sort) {
            case Type.VOID: {
                break;
            }
            case Type.LONG:
            case Type.DOUBLE: {
                dupX2();
                pop();
                pop2();
                break;
            }
            default: {
                swap();
                pop();
                break;
            }
        }
    }
    final protected void processControl(String desc, boolean isPopRawRespond) {
        final Label finishLabel = new Label();
        final Label returnLabel = new Label();
        final Label throwsLabel = new Label();
        /*
         * {rawRespond} 表示 isPopRawRespond = true 时才会存在
         *
         * [Ret, {rawRespond}]
         */
        dup();
        /*
         * [Ret, Ret, {rawRespond}]
         */
        visitFieldInsn(GETFIELD, Type.getType(MethodResult.class).getInternalName(), "state",  Type.getType(int.class).getDescriptor());
        /*
         * [I, Ret, {rawRespond}]
         */
        dup();
        /*
         * [I,I, Ret, {rawRespond}]
         */
        push(MethodResult.RET_STATE_RETURN);
        /*
         * [I,I,I, Ret, {rawRespond}]
         */
        ifICmp(EQ, returnLabel);
        /*
         * [I, Ret, {rawRespond}]
         */
        push(MethodResult.RET_STATE_THROWS);
        /*
         * [I, I, Ret, {rawRespond}]
         */
        ifICmp(EQ, throwsLabel);
        /*
         * [Ret, {rawRespond}]
         */
        goTo(finishLabel);
        mark(returnLabel);
        /*
         * [I, Ret, {rawRespond}]
         */
        pop();
        Type type = Type.getReturnType(desc);
        /*
         * [Ret, {rawRespond}]
         * #fix issue #328
         */
        if (isPopRawRespond) {
            popRawRespond(type);
        }
        /*
         * [Ret]
         */
        visitFieldInsn(GETFIELD, EntireMethodHelper.Result.getInternalName(), "response", Type.getType(Object.class).getDescriptor());
        /*
         *  [spyRespond] ,execute XReturn
         */
        unboxReturn(type);
        /*
         * [spyRespond] Return Exit
         * [spyRespond]
         */
        mark(throwsLabel);
        /*
         * [Ret, {rawRespond}]
         */
        if (isPopRawRespond) {
            popRawRespond(type);
        }
        /*
         * [Ret]
         */
        visitFieldInsn(GETFIELD, EntireMethodHelper.Result.getInternalName(), "response", Type.getType(Object.class).getDescriptor());
        /*
         * [Object]
         */
        checkCast(Type.getType(Throwable.class));
        /*
         * [Throwable]
         */
        throwException();
        /*
         * throw [Throwable] Exit
         */
        mark(finishLabel);
        /*
         * [Ret, {raw respond}]
         */
        pop();
        /*
         * [{raw respond}]
         *  None Exit
         */
    }

}
