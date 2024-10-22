package com.alibaba.push.android.demo

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import kotlinx.coroutines.launch

class AdvanceFuncViewModel(application: Application): AndroidViewModel(application) {

    private val kBoundAccountKey = "bound_account"

    private val kBoundPhoneKey = "bound_phone"

    val showTagAllBtn = SingleLiveData<Boolean>().apply { value = false }

    val showAliasAllBtn = SingleLiveData<Boolean>().apply { value = false }

    val aliasListStr = SingleLiveData<String?>().apply { value =  ""}

    val account = SingleLiveData<String?>()

    val phone = SingleLiveData<String?>()

    val currAliasList = mutableListOf<String>()

    val preferences: SharedPreferences = getApplication<MainApplication>().getSharedPreferences(
        "aliyun_push",
        Context.MODE_PRIVATE
    )

    fun initData(){
        getAliasListFromServer()
        viewModelScope.launch {
            account.value = preferences.getString(kBoundAccountKey, getString(R.string.push_bind_account_no))
            phone.value = preferences.getString(kBoundPhoneKey, getString(R.string.push_bind_phone_no))
        }
    }

    private fun getAliasListFromServer() {
        PushServiceFactory.getCloudPushService().listAliases(object : CommonCallback {
            override fun onSuccess(response: String?) {
                if (TextUtils.isEmpty(response)) {
                    return
                }
                val aliasList = response!!.split(",")
                aliasList.reversed().forEach { alias ->
                    currAliasList.add(alias)
                }
                if (currAliasList.size > 8) {
                    showAliasAllBtn.value = true
                }
                aliasListStr.value = response
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
            }

        })
    }

    fun addAlias(context: Context, alias: String, callback:()->Unit) {
        PushServiceFactory.getCloudPushService().addAlias(alias, object : CommonCallback {
            override fun onSuccess(p0: String?) {
                currAliasList.add(0, alias)
                callback.invoke()
                if (currAliasList.size > 8) {
                    showAliasAllBtn.value = true
                }
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                context.toast(R.string.push_toast_add_alias_fail,errorMessage)
            }

        })
    }

    fun removeAlias(context: Context, alias: String, callback:()->Unit) {
        PushServiceFactory.getCloudPushService().removeAlias(alias, object : CommonCallback {
            override fun onSuccess(p0: String?) {
                currAliasList.remove(alias)
                callback.invoke()
                if (currAliasList.size <= 8) {
                    showAliasAllBtn.value = false
                }
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                context.toast(R.string.push_toast_delete_alias_fail, errorMessage)
            }
        })
    }

    /**
     * 绑定手机号
     */
    fun bindPhone(context: Context, phoneNumber: String) {
        PushServiceFactory.getCloudPushService().bindPhoneNumber(phoneNumber, object:CommonCallback{
            override fun onSuccess(p0: String?) {
                phone.value = phoneNumber
                viewModelScope.launch {
                    val editor = preferences.edit()
                    editor.putString(kBoundPhoneKey, phoneNumber)
                    editor.apply()
                }
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                context.toast(R.string.push_toast_bind_phone_fail, errorMessage)
            }
        })
    }

    /**
     * 绑定账号
     */
    fun bindAccount(context: Context, accountStr: String) {
        PushServiceFactory.getCloudPushService().bindAccount(accountStr, object : CommonCallback {
            override fun onSuccess(p0: String?) {
                account.value = accountStr
                viewModelScope.launch {
                    val editor = preferences.edit()
                    editor.putString(kBoundAccountKey, accountStr)
                    editor.apply()
                }
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                context.toast(R.string.push_bind_account_fail, errorMessage)
            }
        })
    }

    fun alreadyAddAlias(alias: String):Boolean {
        return currAliasList.contains(alias)
    }

    private fun getString(res: Int):String {
        return getApplication<MainApplication>().getString(res)
    }

}