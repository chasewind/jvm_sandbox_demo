package com.deer.agent;

import java.util.ArrayList;
import java.util.List;

public class Span {

    public String traceId;
    public String spanId;
    public Span parentSpan;
    public int sequence;
    public List<Span> childrenList = new ArrayList<>();

}
