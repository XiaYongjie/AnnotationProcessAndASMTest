package com.example.myapplication;

import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HookAMSJava {

    public static void hookAMS() {
        try {
            Field singletonField;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                singletonField = getField(Class.forName("android.app.ActivityManager"), "IActivityManagerSingleton");
            } else {
                singletonField = getField(Class.forName("android.app.ActivityManagerNative"), "gDefault");
            }
            Object singleton = singletonField.get(null);

            Field mInstanceField = getField(Class.forName("android.util.Singleton"), "mInstance");
            final Object mInstance = mInstanceField.get(singleton);

             Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{Class.forName("android.app.IActivityManager")}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    Log.e("tag","-----------"+method.getName());
                    return method.invoke(mInstance, args);
                }
            });
            mInstanceField.set(singleton, proxyInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Field getField(Class<?> forName, String iActivityManagerSingleton) throws NoSuchFieldException {
        Field declaredField = forName.getDeclaredField(iActivityManagerSingleton);
        declaredField.setAccessible(true);
        return  declaredField;

    }
}
