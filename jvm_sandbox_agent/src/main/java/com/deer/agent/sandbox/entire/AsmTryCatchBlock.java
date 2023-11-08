package com.deer.agent.sandbox.entire;

import org.objectweb.asm.Label;

public class AsmTryCatchBlock{

    final Label start;
    final Label end;
    final Label handler;
    final String type;

    public AsmTryCatchBlock(Label start, Label end, Label handler, String type) {
        this.start = start;
        this.end = end;
        this.handler = handler;
        this.type = type;
    }

}