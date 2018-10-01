package com.du;

import com.alibaba.fastjson.JSON;
import com.du.Util.PackageUtil;
import com.du.Util.Param;
import com.du.annotation.Api;
import com.du.annotation.GetPath;
import com.du.annotation.PostPath;
import com.du.controller.UserController;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import jdk.nashorn.internal.ir.ObjectNode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.logging.Logger;

/**
 * @Title 路由分配
 * @ClassName Route
 * @Author jsb_pbk
 * @Date 2018/9/21
 */
public class Route {

   static final Logger logger = Logger.getLogger("Route");

   private static final String GET = "GET";
   private static final String POST = "POST";

    public static  final  String SUCCESS = "成功";
    public static  final  String URI_ERROR = "uri错误";
    public static  final  String METHODERROR = "方法错误";
    public static  final  String INVALID_REQUEST = "无效的请求";
    public static  final  String ERROR_CONTENT_TYPE= "错误的参数类型";


    private static List<RouteEntry> routes;

    static {
        routes = new ArrayList<>();
        /*try {
            routes.add(new RouteEntry(UUID.randomUUID().toString(),"/user","GET",UserController.class ,UserController.class.getMethod("user",String.class,String.class)));
            routes.add(new RouteEntry(UUID.randomUUID().toString(),"/addUser","POST", UserController.class,UserController.class.getMethod("addUser",null)));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }*/
    }

    public static Map<String,Object>  checkRouteUri(Object msg){
        Map<String,Object> retMap  = new HashMap<>(2);
        if(! (msg instanceof FullHttpRequest)){
            retMap.put("state",INVALID_REQUEST);
        }else {
            try {
                FullHttpRequest httpRequest = (FullHttpRequest)msg;
                String[] urlAndParam;
                Object[] params = null;
                if(GET.equals(httpRequest.method().name())){
                    urlAndParam = httpRequest.uri().split("\\?");
                }else {
                    urlAndParam = new String[]{httpRequest.uri()};
                }
                for(RouteEntry entry:routes){
                    if(entry.getUri().equals(urlAndParam[0])){
                        if(entry.getMethod().equals(httpRequest.method().name())){
                            //获取参数
                            if(GET.equals(httpRequest.method().name())){
                                params = getParamFill(getParamAnalysis(urlAndParam[1]),entry.getParams());
                            }else {
                                //post请求
                                String contentType =  httpRequest.headers().get("content-type");
                                if(contentType.contains("form-data")){
                                    params = postFormDataParam(httpRequest,entry.getParams());
                                }else if(contentType.equals("application/json")){
                                    params = postJsonDataParam(httpRequest,entry.getParams());
                                }else {
                                    retMap.put("state",ERROR_CONTENT_TYPE);
                                    return retMap;
                                }
                            }
                            // 是POST请求

                            retMap.put("state",SUCCESS);
                            String p = "";
                            for(Object o:params){
                                p +="["+ o.toString()+"]";
                            }
                            logger.info("请求参数-------------------"+p);
                            String data = null;
                            try {
                                data = entry.getExecute().invoke(entry.getaClass().newInstance(),params).toString();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            retMap.put("data",data);
                            return retMap;
                        }else {
                            retMap.put("state",METHODERROR);
                            return retMap;
                        }
                    }
                }
                retMap.put("state",URI_ERROR);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        return retMap;
    }

    private static Map<String,Object> getParamAnalysis(String params)  {
        String[] ps = params.split("&");
        Map<String,Object> param = new HashMap<>(ps.length);
        String p;
        String[] pp;
        for(int x = 0;x<ps.length;x++){
            p = ps[x];
            pp = p.split("=");
            param.put(pp[0],pp[1]);
        }
        return param;
    }


    private  static Object[]  getParamFill(Map<String,Object> afferentParams,List<Param> params)  {
        List<Object> retList  = new ArrayList<>();
            for(Param param:params){
                Object value = null;
                try {
                    value = decode(afferentParams.get(param.getName())==null?"":afferentParams.get(param.getName()).toString(),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                retList.add(param.getType().cast(value));
            }
            return retList.toArray();
    }

    private static  Object[] postFormDataParam(FullHttpRequest httpRequest,List<Param> params) throws Exception{
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(httpRequest);
        decoder.offer(httpRequest);
        List<InterfaceHttpData> paramList = decoder.getBodyHttpDatas();
        Map<String,String> paramMap = new HashMap<>();
        for (InterfaceHttpData param : paramList) {
            Attribute data = (Attribute) param;
            try {
                paramMap.put(data.getName(), data.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(params.size() == 1){
            return new Object[]{mapToObject(paramMap, params.get(0).getType())};
        }else {
            return null;
        }
    }

    public static Object mapToObject(Map<String, String> map, Class<?> beanClass) throws Exception {
         if (map == null){
             return null;
         }
         Object obj = beanClass.newInstance();
          org.apache.commons.beanutils.BeanUtils.populate(obj, map);
         return obj;
    }


    private static  Object[] postJsonDataParam(FullHttpRequest httpRequest,List<Param> params){
        ByteBuf jsonBuf = httpRequest.content();
        String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
        if(params.size() == 1){
            return new Object[]{JSON.parseObject(jsonStr,params.get(0).getType())};
        }else {
            return null;
        }
    }


    //java类传递url传递中文参数的编码和解码方法
    public static String encode(String str,String charset) throws UnsupportedEncodingException {
        str = java.net.URLEncoder.encode(str, charset);
        return str.replaceAll("%", "_");//在测试中可以直接将转码后的内容放入url里，但是在jsp中得到的字符仍然是乱码，只能将转码后的字符中的%替换成“_”,也可以是其他只要能在转码解码成功就行，曾用“+”但结果仍是无效。
    }

    public static String decode(String str,String charset) throws UnsupportedEncodingException{
        str=str.replaceAll("\\_", "%");
        return java.net.URLDecoder.decode(str, charset);
    }


    public static void routeScan(String classPath){
        List<Class<?>> list = PackageUtil.getClasses(classPath);
        boolean isEmptyClass = false;
        boolean isEmptyMethod = false;
        Method[] methods;
        RouteEntry entry ;
        List<Param> params;
        Param param;
        boolean isGet;
        for(Class<?> c:list){
            isGet = true;
            //判断stu是不是使用了我们刚才定义的注解接口
            isEmptyClass = c.isAnnotationPresent(Api.class);
            if(isEmptyClass){
                //获取类下面的所有方法
                methods = c.getMethods();
                for(Method method:methods){
                    //get请求
                    isEmptyMethod = method.isAnnotationPresent(GetPath.class);
                    String url = "";
                    if(isEmptyMethod){
                        isGet = true;
                        url = method.getAnnotation(GetPath.class).value();
                    }else {
                        isEmptyMethod = method.isAnnotationPresent(PostPath.class);
                        if(!isEmptyMethod ){
                            break;
                        }
                        url = method.getAnnotation(PostPath.class).value();
                        isGet = false;
                    }
                    if(url.indexOf("/")!= 0){
                        url = "/"+url;
                    }
                    if(isEmptyMethod){
                        entry = new RouteEntry();
                        entry.setId(UUID.randomUUID().toString());
                        if(isGet){
                            entry.setMethod("GET");
                        }else {
                            entry.setMethod("POST");
                        }
                        entry.setUri(url);
                        entry.setaClass(c);
                        entry.setExecute(method);
                        params =  new ArrayList<>();
                        // 获取参数类型
                        Parameter[] parameters = method.getParameters();
                        for(Parameter parameter:parameters){
                            com.du.annotation.Param  annotation = parameter.getAnnotation(com.du.annotation.Param.class);
                            if(annotation != null){
                                param = new Param();
                                param.setName(annotation.value());
                                param.setType(parameter.getType());
                                params.add(param);
                            }
                        }
                       /* Class[] paramTypes = method.getParameterTypes();
                        for (Class class2 : paramTypes) {
                            param = new Param();
                            param.setName(class2.getName());
                            param.setType(class2.getComponentType());
                            params.add(param);
                        }*/
                        entry.setParams(params);
                        routes.add(entry);
                    }
                }
            }
        }


        logger.info("list");

    }

}
