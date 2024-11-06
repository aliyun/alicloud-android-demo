package com.alibaba.push.android.demo

import android.app.Application
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

    companion object {
        const val RESPONSE_CHANNEL_OPEN = "on"
    }

    val channelState = SingleLiveData<Boolean>().apply { value = false }

    val divideGroupState = SingleLiveData<Boolean>().apply { value = false }

    val msgReceiveByService = SingleLiveData<Boolean>().apply { value = false }

    val logLevel = SingleLiveData<Int>().apply { value = CloudPushService.LOG_OFF }

    private val pushService = PushServiceFactory.getCloudPushService()

    var tempLogLevel = SingleLiveData<Int>().apply { value = CloudPushService.LOG_OFF }

    var hasRegistered = SingleLiveData<Boolean>().apply { value = false }

    var registerBtnText = SingleLiveData<String>()

    val registerBtnAlpha = SingleLiveData<Float>().apply { value = 1.0f }

    /**
     * 注册
     */
    fun register(){
        if (true == hasRegistered.value) {
            return
        }
        pushService.register(getApplication(), object: CommonCallback{
            override fun onSuccess(response: String?) {
                hasRegistered.value = true
                registerBtnText.value = getApplication<Application>().getString(R.string.push_register_btn_success)
                registerBtnAlpha.value = 0.6f
                getApplication<MainApplication>().toast(R.string.push_toast_register_success)
                getChannelStateFromServer()
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                toast(R.string.push_toast_register_fail, errorMessage)
            }

        })



    }

    /**
     * 获取通道状态
     */
    fun getChannelStateFromServer(){
        pushService.checkPushChannelStatus(object:CommonCallback{
            override fun onSuccess(response: String?) {
                channelState.value = RESPONSE_CHANNEL_OPEN == response
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {

            }

        })
    }

    /**
     * 打开/关闭通知通道
     */
    fun switchPushChannel(){
        if (true == channelState.value) {
            pushService.turnOffPushChannel(object:CommonCallback{
                override fun onSuccess(response: String?) {
                    channelState.value = false
                    toast(R.string.push_toast_close_push_channel_success)
                }

                override fun onFailed(errorCode: String?, errorMessage: String?) {
                    toast(R.string.push_toast_close_push_channel_fail, errorMessage)
                }

            })
        }else {
            pushService.turnOnPushChannel(object:CommonCallback{
                override fun onSuccess(response: String?) {
                    channelState.value = true
                    toast(R.string.push_toast_open_push_channel_success)
                }

                override fun onFailed(errorCode: String?, errorMessage: String?) {
                    toast(R.string.push_toast_open_push_channel_fail, errorMessage)
                }
            })
        }
    }

    /**
     * 是否分组展示通知
     */
    fun switchDivideGroup(){
        divideGroupState.value = !(divideGroupState.value!!)
        pushService.setNotificationShowInGroup(divideGroupState.value!!)
    }

    /**
     * 改变通知接收方式
     */
    fun switchMsgReceiver(){
        msgReceiveByService.value = !msgReceiveByService.value!!
        if (true == msgReceiveByService.value) {
            pushService.setPushIntentService(MyMessageIntentService::class.java)
            toast(R.string.push_toast_use_service_deal_message)
        }else {
            pushService.setPushIntentService(null)
            toast(R.string.push_toast_use_receiver_deal_message)
        }
    }

    /**
     * 清除通知
     */
    fun clearAllNotification(){
        pushService.clearNotifications()
        toast(R.string.push_toast_already_clear)
    }

    /**
     * 设置日志输出等级
     */
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

    private fun toast(res: Int, msg:String? = null) {
        getApplication<MainApplication>().toast(res, msg)
    }

}