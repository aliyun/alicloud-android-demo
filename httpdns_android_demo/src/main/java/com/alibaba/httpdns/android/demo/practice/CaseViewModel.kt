package com.alibaba.httpdns.android.demo.practice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.alibaba.httpdns.android.demo.SingleLiveData

class CaseViewModel(application: Application) : AndroidViewModel(application) {

    val currCaseIndex = SingleLiveData<Int>().apply { value = 0 }

}