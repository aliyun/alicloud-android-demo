package com.alibaba.sophix.demo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wuer on 2017/3/21.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME) //运行时注解信息
@Target(ElementType.METHOD) //只能修饰方法
public @interface TestMethodAnnotation {
    int methodVer() default 10;
    int id() default 1;
    int nnn() default 2;
}
