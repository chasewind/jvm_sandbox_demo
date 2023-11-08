package com.deer.agent.core;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 工具类，单独抽象出来便于问题排查，隔离系统调用产生的问题
 */
public class TraceRecorder {
    private static AtomicLong idStarter = new AtomicLong(1);
    private static ThreadLocal<Trace> traceMap = new ThreadLocal<>();
    private static ThreadLocal<Stack<String>> spanIdRecord = new ThreadLocal<>();
    public static synchronized void before(String className,String methodName,String desc) {

    }
    public static synchronized void before(String methodName) {
        //如果没有构建trace则构建一个
        if (traceMap.get() == null) {
            traceMap.set(new Trace(UUID.randomUUID().toString() + "-" + Thread.currentThread().getId()));
        }
        //构建span
        Trace trace = traceMap.get();
        //入口处方法是第一个span
        Span firstSpan = trace.getFirstSpan();
        if (firstSpan == null) {
            firstSpan = new Span();
            firstSpan.setTraceId(trace.getTraceId());
            firstSpan.setPath(methodName);
            firstSpan.setSpanId(getRootSpanId());
            firstSpan.setEnd(false);
            trace.setFirstSpan(firstSpan);
            //
            trace.setStack(new Stack<>());
            trace.getStack().push(firstSpan);
        } else {
            //当前span
            Span currentSpan = new Span();
            currentSpan.setTraceId(trace.getTraceId());
            currentSpan.setPath(methodName);
            //上下文串联的关键,我们这里实用栈的方式实现
            currentSpan.setSpanId(getNextSpanId());
            currentSpan.setEnd(false);
            trace.getStack().push(currentSpan);
        }


    }

    public static synchronized void after(String methodName) {
        Trace trace = traceMap.get();
        //当前span调用完毕
        //从栈中弹出结束的ID
        Stack<String> stack = spanIdRecord.get();
        if (stack == null || stack.isEmpty()) {
            return;
        } else {
            //弹出
            String spanId = stack.pop();
            //当前span的方法调用结束
            trace.getStack().forEach(currentSpan -> {
                if (currentSpan.getSpanId().equals(spanId)) {
                    currentSpan.setEnd(true);
                }
            });
            trace.setLastSpanId(spanId);

        }
        //说明调用链路执行完毕
        if (stack != null && stack.isEmpty()) {
            //打印树形日志
            trace.printStack();
            //清空数据
            traceMap.remove();
            spanIdRecord.remove();
        }

    }

    /**
     * 根结点特殊设置，理论上可以和getNextSpanId方法合并，这里就是为了提升区分度，比如说每次都生成一个新的spanID
     *
     * @return
     */
    public static String getRootSpanId() {
        Stack<String> stack = new Stack<>();
        long value = idStarter.incrementAndGet();
        //  long value = 1L;
        stack.push(String.valueOf(value));
        spanIdRecord.set(stack);
        return String.valueOf(value);
    }


    public static String getNextSpanId() {
        Stack<String> stack = spanIdRecord.get();
        String spanId = null;
        Trace trace = traceMap.get();
        Stack<Span> spanStack = trace.getStack();
        //逆向遍历当前所有span堆栈，判断是纵向扩展子节点还是横向扩展兄弟节点
        if (spanStack.isEmpty()) {
            //异常情况
            return "1.1";
        }
        List<Span> spanList = new ArrayList<>(spanStack);

        //找到第一个未执行完毕的节点
        //如果栈顶的长度大于第一个未执行的完毕的节点，说明需要构建兄弟节点
        //如果栈顶长度等于第一个未执行完毕的节点，说明需要扩展子节点
        Collections.reverse(spanList);
        Span lastUnFinishedSpan = null;
        for (Span span : spanList) {
            if (span != null && !span.isEnd()) {
                lastUnFinishedSpan = span;
                break;
            }
        }
        Span peekSpan = spanStack.peek();
        String peekSpanId = peekSpan.getSpanId();
        String[] peekArray = StringUtils.split(peekSpanId, "\\.");
        String[] unFinishedArray = StringUtils.split(lastUnFinishedSpan.getSpanId(), "\\.");
        //子节点满的时候，需要在兄弟节点加
        if (peekArray.length > unFinishedArray.length) {
            //横向距离加1
            //把最后一个数字化后加一,判断要在兄弟节点加还是子节点加
            if (peekArray.length - unFinishedArray.length == 1) {
                //在子节点上加
                String value = peekArray[peekArray.length - 1];
                peekArray[peekArray.length - 1] = String.valueOf(Integer.valueOf(value) + 1);
                //
                spanId = StringUtils.join(peekArray, ".");

                stack.push(spanId);
                return spanId;
            } else {
                //在兄弟节点加,堆栈在左侧一个调用特别深的情况下，右侧兄弟节点很难确定是和未完成节点同一级还是和未完成节点的第一子节点同一级
                //不允许在未完成节点的第二以及后续节点，因为堆栈是一层一层结束的,新的节点也不允许跨层进，跨层出

                //先判断最后一次弹出的栈层所对应的spanId
                String lastSpanId = trace.getLastSpanId();
                //我们举例说明，假如一个节点2.1.1内部调用非常深，当这些调用一层层回来的时候，优先处理的是 2.1.1同层级的数据然后再处理上一层
                //从数学角度来说，此时要新增节点，2.1.2 是可以的，2.2也是可以的
                //从程序调用角度看，要判断同层级的是否彻底处理完（2.1.1堆栈被弹出），上一层级(2.1)如果没有被弹出，说明目前仍在同层级
                String[] array = StringUtils.split(stack.peek(), "\\.");
                String[] before = StringUtils.split(lastSpanId, "\\.");
                //所以，这里的判断逻辑就是 层级差等于1
                if (array.length == before.length - 1) {
                    //把最后一个数字化后加一
                    String value = before[before.length - 1];
                    before[before.length - 1] = String.valueOf(Integer.valueOf(value) + 1);
                    //
                    spanId = StringUtils.join(before, ".");
                    stack.push(spanId);
                    return spanId;
                }
                // 大概率以下代码不执行
                String value = unFinishedArray[unFinishedArray.length - 1];
                unFinishedArray[unFinishedArray.length - 1] = String.valueOf(Integer.valueOf(value) + 1);
                //
                spanId = StringUtils.join(unFinishedArray, ".");
                stack.push(spanId);
                return spanId;
            }


        } else if (peekArray.length == unFinishedArray.length) {
            //路径长度加1
            spanId = lastUnFinishedSpan.getSpanId() + ".1";
            stack.push(spanId);
            return spanId;
        } else {
            //异常情况
            return "1.1";
        }
    }
}
