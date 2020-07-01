package com.example.myapplication

import android.content.Context
import dalvik.system.DexClassLoader
import java.lang.reflect.Array

object AppPluginUtils{
    @JvmStatic
    fun installApkPlugin(context: Context){
        var path  = "/sdcard/apkplugin-debug.apk"
        try {

            val pathClassLoader = context.classLoader
            val baseLoader =  pathClassLoader.javaClass.superclass
            baseLoader?.let {
                val appPathList =  it.getDeclaredField("pathList")
                appPathList.isAccessible = true
                val appDexElements = appPathList.type.getDeclaredField("dexElements")
                appDexElements.isAccessible  = true

                var elements = appDexElements.get(appPathList.get(pathClassLoader)) as kotlin.Array<Any>

                val dexClassLoader =DexClassLoader(path,context.cacheDir.absolutePath,null,null)

                val pluginClassLoader =  dexClassLoader.javaClass.superclass

                pluginClassLoader?.let { pl ->
                    val pluginPathList =  pl.getDeclaredField("pathList")
                    pluginPathList.isAccessible = true

                    val pluginDexElements = pluginPathList.type.getDeclaredField("dexElements")
                    pluginDexElements.isAccessible  = true

                    val pluginElements = appDexElements.get(pluginPathList.get(dexClassLoader)) as kotlin.Array<Any>


                    val newElements = Array.newInstance(appDexElements.type.componentType as Class<*>,elements.size+pluginElements.size)
                    System.arraycopy(elements,0,newElements,0,elements.size)
                    System.arraycopy(pluginElements,0,newElements,elements.size,pluginElements.size)

                    appDexElements.set(appPathList.get(pathClassLoader),newElements)
                }

            }




        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}