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
            //这里只处理了上下层级的数据，同一层级的还没有处理,在数据结构上就需要再填部分信息，识别出同级节点关联关系
            //使用天然的栈调用结构，这些逻辑就不用考虑了，只需保证子节点是倒序查询即可
            Span parent =  findSpanBySequenceId(trace.firstSpan,parentSequence);
            if(parent != null){
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

        //根据下标，倒序遍历查询childrenList,这样来确保和栈行为一致，正序进入，倒序退出
        for (int i = root.childrenList.size() - 1; i >= 0; i--) {
            Span child=root.childrenList.get(i);
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
