package com.du;

import com.du.Util.Param;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Title  路由对象
 * @ClassName RouteEntry
 * @Author jsb_pbk
 * @Date 2018/9/21
 */
public class RouteEntry {
    private String id;
    private String uri;
    private String method;
    private Class<?>  aClass;
    private Method execute;
    private List<Param> params;


    public RouteEntry(){
        super();
    }

    public RouteEntry(String id,String uri,String method,Class<?>  aClass,Method execute,List<Param> params){
        this.id = id;
        this.uri = uri;
        this.method = method.toUpperCase();
        this.aClass = aClass;
        this.execute = execute;
        this.params = params;
    }


    public List<Param> getParams() {
        return params;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Method getExecute() {
        return execute;
    }

    public void setExecute(Method execute) {
        this.execute = execute;
    }
}
