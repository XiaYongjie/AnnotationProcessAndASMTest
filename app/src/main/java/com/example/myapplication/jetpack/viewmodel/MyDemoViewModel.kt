package com.example.myapplication.jetpack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MyDemoViewModel(myApplication: Application) : AndroidViewModel(myApplication) {
    private var mLiveData:MutableLiveData<String> = MutableLiveData()

    fun setData(text:String){
        mLiveData.value = text
    }

    fun getData():MutableLiveData<String>{
        return mLiveData
    }
}