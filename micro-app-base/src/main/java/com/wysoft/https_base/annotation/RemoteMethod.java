package com.wysoft.https_base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义分布式方法的注解.
 * 被该注解标记的方法，会发布到分布式目录下，供调用方查询。
 * @author Wuyong
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoteMethod {
	String value() default "";
}
