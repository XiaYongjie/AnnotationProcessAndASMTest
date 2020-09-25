package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.jetpack.LiveDataActivity
import com.example.test_annotation.MyRouter
import kotlinx.android.synthetic.main.activity_main.*

@MyRouter(router = "/main")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hello.setOnClickListener {
            //启动插件代码
//            startActivity(Intent(this, Class.forName("com.example.apkplugin.MainActivity")))
//            AppPluginUtils.installApkPlugin(applicationContext)
//            try {
//                val myClass  = classLoader.loadClass("com.example.apkplugin.Test")
//                var test =  myClass.newInstance()
//                var method = test.javaClass.getMethod("print")
//                method.invoke(test)
//
//            }catch (e:Exception){
//                e.printStackTrace()
//            }
        }
        live_data.setOnClickListener {
            startActivity(Intent(this,LiveDataActivity::class.java))
        }
    }
}