package com.du.annotation;

import java.lang.annotation.*;

/**
 * @Title
 * @ClassName Api
 * @Author jsb_pbk
 * @Date 2018/9/21
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {
    String value();
}
