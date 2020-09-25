package com.example.myapplication.jetpack

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.jetpack.viewmodel.MyDemoViewModel
import kotlinx.android.synthetic.main.activity_live_data.*

class LiveDataActivity : AppCompatActivity(){
    private lateinit var mModel:MyDemoViewModel
    private var count =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_data)
        mModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(MyDemoViewModel::class.java)
        bt_1.setOnClickListener {
            mModel.setData(count++.toString())
        }
        mModel.getData().observe(this){
            Log.e("TAG", "text: $it")
        }


    }

    override fun getLifecycle(): Lifecycle {
        return super.getLifecycle()
    }
}