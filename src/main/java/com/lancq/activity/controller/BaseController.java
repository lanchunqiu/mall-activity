package com.lancq.activity.controller;

/**
 * @Author lancq
 * @Description
 * @Date 2018/7/8
 **/
public class BaseController {
    static  ThreadLocal<String> uidThreadLocal=new ThreadLocal<>();

    public void setUid(String uid){
        uidThreadLocal.set(uid);
    }
    public String getUid(){
        return  uidThreadLocal.get();
    }

}
