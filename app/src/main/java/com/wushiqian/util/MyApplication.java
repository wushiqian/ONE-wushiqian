package com.wushiqian.util;

import android.app.Application;
import android.content.Context;

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
