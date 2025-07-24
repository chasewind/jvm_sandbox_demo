package com.deer.trace.agent.transformers;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.JavaModule;

import java.security.ProtectionDomain;

/**
 * 自定义转换器父类，规范子类注入的形式
 */
public abstract class AbstractSelfDefineTransformer implements  AgentBuilder.Transformer{

    /**
     * 类名适配
     */
   public  abstract ElementMatcher<? super TypeDescription> clazzMatcher();

    public abstract ElementMatcher<? super MethodDescription> methodMatcher();
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
        return builder;
    }
}
