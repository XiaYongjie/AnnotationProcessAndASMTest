package com.example.myapplication.coroutines

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_coroutines.*
import kotlinx.coroutines.*

class CoroutinesActivity : AppCompatActivity() , CoroutineScope by MainScope() {
    var tv:TextView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutines)
        bt_1.setOnClickListener {
                launch (Dispatchers.IO){
                    Log.e("TAG","Thread name :${Thread.currentThread().name}")
                    withContext(Dispatchers.Main){
                        Log.e("TAG","Thread withContext :${Thread.currentThread().name}")
                        bt_1.text="111111111111"
                    }

                }
        }
        bt_2.setOnClickListener {
            val job = launch {
                val deferred = async(Dispatchers.IO) {
                    getDelyString()
                }
                val deferred2 = async(Dispatchers.IO) {
                    getDelyString()
                }
                val value = deferred.await()
                val value2 = deferred2.await()
                Log.e("TAG", "----value ${value}-----")
            }
        }
        Thread{
            tv= TextView(this@CoroutinesActivity)
            runOnUiThread{
                layout1.addView(tv)
                Log.e("TAG","----------------------------")
            }
            Thread.sleep(1000)
            tv?.setText("-------------------------------")

        }.start()

    }

    suspend fun getDelyString():String{
        delay(1000)
        Log.e("TAG","Thread getDelyString :${Thread.currentThread().name}")
        return "HelloWord"

    }

    suspend fun getDelyString2():String{
        delay(1000)
        Log.e("TAG","Thread getDelyString :${Thread.currentThread().name}")
        return "HelloWord2"

    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}