package alibaba.httpdns_android_demo.best_practice

import alibaba.httpdns_android_demo.SingleLiveData
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class CaseViewModel(application:Application):AndroidViewModel(application) {

    val currCaseIndex = SingleLiveData<Int>().apply { value = 0 }

}