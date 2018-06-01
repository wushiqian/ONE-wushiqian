package com.wushiqian.util;

import android.app.Application;
import android.content.Context;

/**
* 全局获取context
* @author wushiqian
* created at 2018/5/27 1:11
*/
public class MyApplication extends Application{

    private static Context sContext;

    @Override
    public void onCreate(){
        sContext = getApplicationContext();
    }

    public static Context getContext(){
        return sContext;
    }
}
