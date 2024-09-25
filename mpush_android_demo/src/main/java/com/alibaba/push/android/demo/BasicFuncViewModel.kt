package com.alibaba.push.android.demo

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.alibaba.sdk.android.push.CloudPushService
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory

/**
 * 基础功能的ViewModel
 * @author ren
 * @date 2024/09/20
 */
class BasicFuncViewModel(application: Application): AndroidViewModel(application) {

    val channelState = SingleLiveData<Boolean>().apply { value = false }

    val divideGroupState = SingleLiveData<Boolean>().apply { value = false }

    val msgReceiveByService = SingleLiveData<Boolean>().apply { value = false }

    val logLevel = SingleLiveData<Int>().apply { value = CloudPushService.LOG_OFF }

    private val pushService = PushServiceFactory.getCloudPushService()

    var tempLogLevel = SingleLiveData<Int>().apply { value = CloudPushService.LOG_OFF }

    fun register(){
        pushService.register(getApplication(), object: CommonCallback{
            override fun onSuccess(response: String?) {
                channelState.value = true
                toast(getApplication<MainApplication>().getString(R.string.push_toast_register_success))
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                toast(String.format(getApplication<MainApplication>().getString(R.string.push_toast_register_fail), errorMessage))
            }

        })
    }

    fun switchPushChannel(){
        if (true == channelState.value) {
            pushService.turnOffPushChannel(object:CommonCallback{
                override fun onSuccess(response: String?) {
                    channelState.value = false
                    toast(getApplication<MainApplication>().getString(R.string.push_toast_close_push_channel_success))
                }

                override fun onFailed(errorCode: String?, errorMessage: String?) {
                    toast(String.format(getApplication<MainApplication>().getString(R.string.push_toast_close_push_channel_fail),errorMessage))
                }

            })
        }else {
            pushService.turnOnPushChannel(object:CommonCallback{
                override fun onSuccess(response: String?) {
                    channelState.value = true
                    toast(getApplication<MainApplication>().getString(R.string.push_toast_open_push_channel_success))
                }

                override fun onFailed(errorCode: String?, errorMessage: String?) {
                    toast(String.format(getApplication<MainApplication>().getString(R.string.push_toast_open_push_channel_fail), errorMessage))
                }
            })
        }
    }

    fun switchDivideGroup(){
        divideGroupState.value = !(divideGroupState.value!!)
        pushService.setNotificationShowInGroup(divideGroupState.value!!)
    }

    fun switchMsgReceiver(){
        msgReceiveByService.value = !msgReceiveByService.value!!
        if (true == msgReceiveByService.value) {
            pushService.setPushIntentService(MyMessageIntentService::class.java)
        }else {
            pushService.setPushIntentService(null)
        }
    }

    fun clearAllNotification(){
        pushService.clearNotifications()
    }

    fun setLogLevel(){
        if (tempLogLevel.value == logLevel.value) {
            return
        }
        logLevel.value = tempLogLevel.value
        pushService.setLogLevel(tempLogLevel.value?:CloudPushService.LOG_OFF)
    }

    fun setTempLogLevel(level: Int) {
        if (level == tempLogLevel.value) {
            return
        }
        tempLogLevel.value = level
    }

    private fun toast(msg: String) {
        Toast.makeText(getApplication(),msg,Toast.LENGTH_SHORT).show()
    }

}