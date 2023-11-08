package com.deer.agent.sandbox.trace;


public class Span {
    private String traceId;
    private String spanId;
    /**方法名+类名,可以定义的更复杂一些，比如类中有同名不同参方法区分不了，不过演示demo会写的简单一些*/
    private String path;
    /**当前方法是否执行完毕*/
    private boolean end;

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
