package com.deer.agent.sandbox.trace;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 一次完整的调用链路
 */
public class Trace {
    private String traceId;
    private Span firstSpan;
    private Stack<Span> stack;

    public String getLastSpanId() {
        return lastSpanId;
    }

    public void setLastSpanId(String lastSpanId) {
        this.lastSpanId = lastSpanId;
    }

    private String lastSpanId;

    public Trace() {
    }

    public Trace(String traceId) {
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Span getFirstSpan() {
        return firstSpan;
    }

    public void setFirstSpan(Span firstSpan) {
        this.firstSpan = firstSpan;
    }

    public Stack<Span> getStack() {
        return stack;
    }

    public void setStack(Stack<Span> stack) {
        this.stack = stack;
    }

    public void printStack() {
        System.out.println("\ttraceId:\t" + traceId + "\t 调用链路如下------");
        //打印为树形结构
        List<Span> spanList = new ArrayList<>();
        if (!stack.isEmpty()) {
            spanList.addAll(stack);
        }
        //排序
        spanList.sort((first, second) -> {
            return first.getSpanId().compareTo(second.getSpanId());
        });
        spanList.forEach(span -> {
            int arrayLen = StringUtils.split(span.getSpanId(), "\\.").length;
            String gap = "";
            for (int i = 0; i < arrayLen; i++) {
                gap += "\t";
            }
            System.out.println("\t" + gap + span.getSpanId() + "," + span.getPath() + "----");
        });
        System.out.println();
    }
}
