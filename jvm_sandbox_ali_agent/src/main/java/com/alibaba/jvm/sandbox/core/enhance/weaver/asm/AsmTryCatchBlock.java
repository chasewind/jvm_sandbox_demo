package com.alibaba.jvm.sandbox.core.enhance.weaver.asm;

import org.objectweb.asm.Label;

/**
 * TryCatch块,用于ExceptionsTable重排序
 */

public class AsmTryCatchBlock {
    final Label start;
    final Label end;
    final Label handler;
    final String type;

    AsmTryCatchBlock(Label start, Label end, Label handler, String type) {
        this.start = start;
        this.end = end;
        this.handler = handler;
        this.type = type;
    }
}
