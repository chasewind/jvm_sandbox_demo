package com.deer.agent;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Callable;

public class SpringControllerInterceptor extends PreciseInterceptor {
    @Override
    public String getDescription() {
        return "拦截spring 的Controller，RestController，请尽可能的使用项目对应的包名，避免对spring相关包的依赖";
    }

    @RuntimeType
    public static Object intercept(@Origin Method method, @AllArguments Object[] args, @SuperCall Callable<?> callable) throws Exception {
        try {
            //代码入口处是标准的方法定义，不可能起步就是线程
            //如果是同一个类，内部方法A起线程调用方法B，此时就需要尝试找线程数据，把线程数据也拼接回来
            OpenTrace openTrace = OpenTraceContext.getTrace();
            Span currentSpan = null;
            boolean isEntrance = false;
            //当前span为空，则为首次进入
            //首次进入记录为空
            if (openTrace == null) {
                isEntrance = true;
                openTrace = new OpenTrace();
                openTrace.traceId = UUID.randomUUID().toString();
                Span firstSpan = new Span();
                firstSpan.traceId = openTrace.traceId;
                firstSpan.spanId = method.getDeclaringClass().getName() + "." + method.getName();
                OpenTraceContext.setTrace(openTrace);
                OpenTraceManager.addTrace(openTrace.traceId ,openTrace);
                //
                currentSpan = firstSpan;
            } else {
                //涉及到被调用,需要新建span并补充到trace中
                Span span = new Span();
                span.traceId = openTrace.traceId;
                span.spanId = method.getDeclaringClass().getName() + "." + method.getName();
                currentSpan = span;
                //找到上一级Span,判断是新增子级Span还是兄弟级Span,并安排到合适的位置
                //这里利用java的特性来处理 假如A调用B，B调用C 和D，构成如下调用关系
                //嵌套层级可以通过深度直接来，但是C和D这种平级需要通过parent节点来处理
                //---A(before)
                //------B(before)
                //---------C(before)
                //---------C(after)
                //---------D(before)
                //---------D(after)
                //------B(after)
                //---A(after)

            }
            //以下是标准模式 入口--->上下文处理--->出口
            //入口
            OpenTraceContext.startDepth();
            //上下文处理
            OpenTraceContext.setSpan(currentSpan);
            System.out.println("web "+(isEntrance?"入口 : ":"被调用 : ") + openTrace.traceId +" span: "+currentSpan.spanId);
            // 原有函数执行
            Object ret = callable.call();
            //出口
            OpenTraceContext.exitDepth();
            //打印调用轨迹
            OpenTraceContext.printTrace();
            OpenTraceContext.clearTrace();
            return ret;
        } catch (Exception e) {
            throw e;
        }
    }

}
