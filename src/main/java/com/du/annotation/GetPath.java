package com.du.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Title
 * @ClassName Api
 * @Author jsb_pbk
 * @Date 2018/9/21
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GetPath {
    String value() default "/";
}
