package com.alibaba.jvm.sandbox.api.annotation;

import com.alibaba.jvm.sandbox.api.filter.Filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 拥有此标注的{@link Filter}将能匹配到目标类的子类
 *
 * @author luanjia@taobao.com
 * @since {@code sandbox-api:1.0.10}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface IncludeSubClasses {
}
