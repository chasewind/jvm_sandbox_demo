package com.deer.trace.agent.transformers.jdk;

import com.deer.trace.agent.transformers.AbstractSelfDefineTransformer;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class JdkConsumerTransformer extends AbstractSelfDefineTransformer {
    @Override
    public ElementMatcher<? super TypeDescription> clazzMatcher() {
        return named("com.deer.special.ConsumerWrapper");
    }

    @Override
    public ElementMatcher<? super MethodDescription> methodMatcher() {
        //TODO 待定
        return named("accept");
    }
}
