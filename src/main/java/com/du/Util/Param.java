package com.du.Util;

import java.io.Serializable;

/**
 * @Title
 * @ClassName Param
 * @Author jsb_pbk
 * @Date 2018/9/21
 */
public class Param implements Serializable {
    private Class<?> type;
    private String name;

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
