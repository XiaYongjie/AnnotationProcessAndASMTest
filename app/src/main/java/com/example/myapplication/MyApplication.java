package com.example.myapplication;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HookAMSForP.INSTANCE.hookAMS();
//        HookAMSJava.hookAMS();
//        AppPluginUtils.installApkPlugin(getApplicationContext());
    }
}
