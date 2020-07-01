package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test_annotation.MyRouter
import kotlinx.android.synthetic.main.activity_main.*

@MyRouter(router = "/main")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hello.setOnClickListener {
            AppPluginUtils.installApkPlugin(applicationContext)
            try {
                val myClass  = classLoader.loadClass("com.example.apkplugin.Test")
                var test =  myClass.newInstance()
                var method = test.javaClass.getMethod("print")
                method.invoke(test)

            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}