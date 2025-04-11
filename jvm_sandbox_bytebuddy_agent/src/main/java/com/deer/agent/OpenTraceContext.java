package com.deer.agent;

public class OpenTraceContext {
    private static final ThreadLocal<OpenTrace> TRACK_LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<Span> SPAN_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Integer> CALL_DEPTH = new ThreadLocal<>();


    public static void clearTrace() {
        TRACK_LOCAL.remove();
    }

    public static OpenTrace getTrace() {
        return TRACK_LOCAL.get();
    }

    public static void setTrace(OpenTrace openTrace) {
        TRACK_LOCAL.set(openTrace);
    }

    public static void clearSpan() {
        SPAN_LOCAL.remove();
    }

    public static Span getSpan() {
        return SPAN_LOCAL.get();
    }

    public static void setSpan(Span span) {

        //调整树形结构
        int currentDepth = CALL_DEPTH.get();
        span.sequence = currentDepth;
        int parentSequence = currentDepth-1;
        OpenTrace trace = getTrace();
        //从根节点开始处理
        if (trace.firstSpan == null) {
            span.sequence = 1;
            trace.firstSpan = span;
        }else{
            Span parent =  findSpanBySequenceId(trace.firstSpan,parentSequence);
            if(parent!=null){
                span.parentSpan = parent;
                parent.childrenList.add(span);
            }
        }
        //放到上下文中
        SPAN_LOCAL.set(span);
    }


    public static Span findSpanBySequenceId(Span root, int sequence) {
        if (root == null) {
            return null;
        }
        if (root.sequence== sequence) {
            return root;
        }

        // 遍历当前节点的所有子节点
        for (Span child : root.childrenList) {
            Span result = findSpanBySequenceId(child, sequence);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public static void startDepth() {
        int currentDepth = CALL_DEPTH.get() != null ? CALL_DEPTH.get() : 0;
        CALL_DEPTH.set(currentDepth + 1);
    }

    public static void exitDepth() {
        int currentDepth = CALL_DEPTH.get() != null ? CALL_DEPTH.get() : 0;
        CALL_DEPTH.set(currentDepth - 1);

    }

    public static void printTrace() {

        OpenTrace trace = getTrace();
        if(trace != null){
            System.out.println("---------------start----------------");
            printSpanTree(trace.firstSpan,0);
            System.out.println("---------------end----------------");
        }
    }
    public static void printSpanTree(Span root, int depth) {
        if (root == null) {
            return;
        }

        // 打印当前节点，使用缩进表示层级
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("  "); // 每一层缩进两个空格
        }
        System.out.println(indent + "SpanId: " + root.spanId + ", Sequence: " + root.sequence);

        // 递归打印子节点
        for (Span child : root.childrenList) {
            printSpanTree(child, depth + 1); // 深度加一
        }
    }
}
