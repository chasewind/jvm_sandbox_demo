package com.deer.trace.agent.transformers.jdk;

import com.deer.trace.agent.transformers.AbstractSelfDefineTransformer;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class JdkRunnableTransformer extends AbstractSelfDefineTransformer {
    @Override
    public ElementMatcher<? super TypeDescription> clazzMatcher() {
        return named("com.deer.special.RunnableWrapper");
    }

    @Override
    public ElementMatcher<? super MethodDescription> methodMatcher() {
        //这是一个无参构造函数，逻辑简单
        return named("run");
    }


}
