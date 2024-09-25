package com.alibaba.push.android.demo

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class MainViewModel(application: Application):AndroidViewModel(application) {

    val fragmentIndex = SingleLiveData<Int>().apply { value = 0 }
    var tabClickCallBack:((Int)->Unit)? = null
    fun tabClick(position: Int) {
        fragmentIndex.value = position
        tabClickCallBack?.invoke(position)
    }

}