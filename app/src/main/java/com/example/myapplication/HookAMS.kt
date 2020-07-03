package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.util.Log
import java.lang.reflect.Field
import java.lang.reflect.Proxy

object HookAMS {
    private const val KEY_INTENT ="key_intent"
    fun hookAMS(context: Context) {
        try {
            val singletonField = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getField(
                    Class.forName("android.app.ActivityManager"), "IActivityManagerSingleton")
            } else {
                getField(Class.forName("android.app.ActivityManagerNative"), "gDefault")
            }
            val singleton = singletonField[null]
            val mInstanceField =
                getField(Class.forName("android.util.Singleton"), "mInstance")
            val mInstance = mInstanceField[singleton]
            val proxyInstance = Proxy.newProxyInstance(Thread.currentThread().contextClassLoader,
                arrayOf(Class.forName("android.app.IActivityManager"))) { proxy, method, args ->
                if (method.name == "startActivity"){
                    Log.e("3","--------------4------------")
                    var mIntent: Intent?=null
                    var mIndex = -1
                    for ((index, arge) in args.withIndex()){
                        if (arge is Intent){
                            mIntent = arge
                            mIndex = index
                            break
                        }
                    }
                    if (mIndex!=-1){
                        args[mIndex] = Intent(context,HookActivity::class.java).apply {
                            putExtra(KEY_INTENT,mIntent)
                        }
                    }

                }
                method.invoke(mInstance, *args)
            }
            mInstanceField[singleton] = proxyInstance
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hookHandle(){
        val mHFiled = getField(Class.forName("android.app.ActivityThread"), "mH")
        val mSCurrentActivityThreadFiled = getField(Class.forName("android.app.ActivityThread"), "sCurrentActivityThread")
        val mActivityThread = mSCurrentActivityThreadFiled.get(null)
        val mH = mHFiled.get(mActivityThread)
        val mCallbackField = getField(Class.forName("android.os.Handler"), "mCallback")
        mCallbackField.set(mH,Handler.Callback {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                if(it.what==159){
                    try {
                        val mActivityCallbacksField =
                            getField(it.obj.javaClass, "mActivityCallbacks")
                        val mActivityCallbacks =
                            mActivityCallbacksField[it.obj] as List<*>
                        for (i in mActivityCallbacks.indices) {
                            if (mActivityCallbacks[i]!!.javaClass.name
                                == "android.app.servertransaction.LaunchActivityItem"
                            ) {
                                val launchActivityItem = mActivityCallbacks[i]!!
                                val mIntentField =
                                    getField(launchActivityItem.javaClass, "mIntent")
                                val intent = mIntentField[launchActivityItem] as Intent
                                // 获取插件的
                                val proxyIntent =
                                    intent.getParcelableExtra<Intent>(KEY_INTENT)
                                //替换
                                if (proxyIntent != null) {
                                    mIntentField[launchActivityItem] = proxyIntent
                                }
                            }
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }else{
                if (it.what==100){
                    try {
                        val intentField =
                            getField(it.obj.javaClass, "intent")
                        val proxyIntent = intentField[it.obj] as Intent
                        val targetIntent =
                            proxyIntent.getParcelableExtra<Intent>(KEY_INTENT)
                        if (targetIntent != null) {
                            intentField[it.obj] = targetIntent
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
            false
        })
    }



    @Throws(NoSuchFieldException::class)
    private fun getField(forName: Class<*>, iActivityManagerSingleton: String): Field {
        val declaredField =
            forName.getDeclaredField(iActivityManagerSingleton)
        declaredField.isAccessible = true
        return declaredField
    }
}