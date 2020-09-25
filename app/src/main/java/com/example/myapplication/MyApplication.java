package com.example.myapplication;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 插件化练习
//        AppPluginUtils.installApkPlugin(getApplicationContext());
//        HookAMS.INSTANCE.hookAMS(getApplicationContext());
//        HookAMS.INSTANCE.hookHandle();
    }
}
