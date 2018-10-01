package com.du.controller;

import com.du.annotation.Api;
import com.du.annotation.GetPath;
import com.du.annotation.Param;
import com.du.annotation.PostPath;
import com.du.pojo.UserEntry;

import java.util.List;

/**
 * @Title
 * @ClassName UserController
 * @Author jsb_pbk
 * @Date 2018/9/21
 */
@Api
public class UserController {
    @GetPath(value = "/user")
    public String user(@Param("uid") String uid, @Param("name") String name){
        return "user接口:uid="+uid+"----name="+name;
    }
    @PostPath(value = "addUser")
    public String addUser(@Param("data") UserEntry data){
        return "addUser接口====参数："+data.toString();
    }

    @PostPath(value = "listUser")
    public String listUser(@Param("list") List<UserEntry> data){
        return "addUser接口====参数："+data.toString();
    }
}
