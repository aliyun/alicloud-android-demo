package com.alibaba.push.android.demo

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.sdk.android.push.CloudPushService
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class AdvanceFuncViewModel(application: Application) : AndroidViewModel(application) {

    val showMoreTagCount = 9

    val showAliasAllBtn = SingleLiveData<Boolean>().apply { value = false }

    val aliasListStr = SingleLiveData<String?>().apply { value = "" }

    val hasTag = SingleLiveData<Boolean>().apply { value = false }

    val hasDeviceTag = SingleLiveData<Boolean>().apply { value = false }

    val hasAliasTag = SingleLiveData<Boolean>().apply { value = false }

    val hasAccountTag = SingleLiveData<Boolean>().apply { value = false }

    val showMoreDeviceTag = SingleLiveData<Boolean>().apply { value = false }

    val showMoreAliasTag = SingleLiveData<Boolean>().apply { value = false }

    val showMoreAccountTag = SingleLiveData<Boolean>().apply { value = false }

    val account = SingleLiveData<String?>()

    val phone = SingleLiveData<String?>()

    val currAliasList = mutableListOf<String>()

    val deviceTagData = SingleLiveData<String>().apply { value = "" }

    val aliasTagData = SingleLiveData<String>().apply { value = "" }

    val accountTagData = SingleLiveData<String>().apply { value = "" }

    val deviceTags = mutableListOf<String>()

    val aliasTags = mutableListOf<String>()

    val accountTags = mutableListOf<String>()

    private var aliasTagMap = mutableMapOf<String, String>()

    val showDividerDeviceTag = SingleLiveData<Boolean>().apply { value = false }

    val showDividerAliasTag = SingleLiveData<Boolean>().apply { value = false }

    private val gson = Gson()

    val preferences: SharedPreferences = getApplication<MainApplication>().getSharedPreferences(
        "aliyun_push",
        Context.MODE_PRIVATE
    )

    fun initData() {
        getAliasListFromServer()
        viewModelScope.launch {
            account.value =
                preferences.getString(SP_KEY_BIND_ACCOUNT, getString(R.string.push_bind_account_no))
            phone.value =
                preferences.getString(SP_KEY_BIND_PHONE, getString(R.string.push_bind_phone_no))
        }
        getDeviceTagFromServer()
        getAliasTagFromSp()
        getAccountTagFromSp()
    }

    private fun getAccountTagFromSp() {
        val spAccountTagData = preferences.getString(SP_KEY_ACCOUNT_TAG, "")
        if (TextUtils.isEmpty(spAccountTagData)) return
        spAccountTagData!!.split(",").forEach {
            accountTags.add(0, it)
        }
        updateTagStatus()
        accountTagData.value = spAccountTagData
    }

    private fun getAliasTagFromSp() {
        val spAliasTagData = preferences.getString(SP_KEY_ALIAS_TAG, "")
        if (TextUtils.isEmpty(spAliasTagData)) return
        spAliasTagData!!.split(",").forEach {
            aliasTags.add(0, it)
        }
        updateTagStatus()
        aliasTagData.value = spAliasTagData
        val spAliasTagMap = preferences.getString(SP_KEY_ALIAS_TAG_MAP, "")
        aliasTagMap =
            gson.fromJson(spAliasTagMap, object : TypeToken<Map<String, String>>() {}.type)

    }

    private fun getDeviceTagFromServer() {
        PushServiceFactory.getCloudPushService()
            .listTags(CloudPushService.DEVICE_TARGET, object : CommonCallback {
                override fun onSuccess(response: String?) {
                    if (TextUtils.isEmpty(response)) return
                    response?.let {
                        val tagList = it.split(",")
                        tagList.forEach { deviceTag ->
                            deviceTags.add(0, deviceTag)
                        }
                        updateTagStatus()
                        deviceTagData.value = response

                    }
                }

                override fun onFailed(p0: String?, p1: String?) {

                }
            })
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

    fun addAlias(context: Context, alias: String, callback: () -> Unit) {
        PushServiceFactory.getCloudPushService().addAlias(alias, object : CommonCallback {
            override fun onSuccess(p0: String?) {
                currAliasList.add(0, alias)
                callback.invoke()
                if (currAliasList.size > 8) {
                    showAliasAllBtn.value = true
                }
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                context.toast(R.string.push_toast_add_alias_fail, errorMessage)
            }
        })
    }

    fun removeAlias(context: Context, alias: String, callback: () -> Unit) {
        PushServiceFactory.getCloudPushService().removeAlias(alias, object : CommonCallback {
            override fun onSuccess(p0: String?) {
                updateTagAfterRemoveAlias(alias)
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

    private fun updateTagAfterRemoveAlias(alias: String){
        aliasTagMap[alias]?.split(",")?.forEach {
            aliasTags.remove(it)
        }
        aliasTagData.value = aliasTags.joinToString(",")
        updateTagStatus()
        aliasTagMap.remove(alias)
        viewModelScope.launch {
            val editor = preferences.edit()
            editor.putString(SP_KEY_ALIAS_TAG, aliasTagData.value)
            editor.putString(SP_KEY_ALIAS_TAG_MAP, gson.toJson(aliasTagMap))
            editor.apply()
        }

    }

    /**
     * 绑定手机号
     */
    fun bindPhone(context: Context, phoneNumber: String) {
        PushServiceFactory.getCloudPushService()
            .bindPhoneNumber(phoneNumber, object : CommonCallback {
                override fun onSuccess(p0: String?) {
                    phone.value = phoneNumber
                    viewModelScope.launch {
                        val editor = preferences.edit()
                        editor.putString(SP_KEY_BIND_PHONE, phoneNumber)
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
                updateTagAfterBindAccount()
                account.value = accountStr
                viewModelScope.launch {
                    val editor = preferences.edit()
                    editor.putString(SP_KEY_BIND_ACCOUNT, accountStr)
                    editor.apply()
                }
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                context.toast(R.string.push_bind_account_fail, errorMessage)
            }
        })
    }

    private fun updateTagAfterBindAccount(){
        accountTags.clear()
        accountTagData.value = ""
        updateTagStatus()
        viewModelScope.launch {
            val editor = preferences.edit()
            editor.putString(SP_KEY_ACCOUNT_TAG, accountTagData.value)
            editor.apply()
        }
    }

    fun addTag(tag: String, target: Int, alias: String?) {
        if ((target == CloudPushService.DEVICE_TARGET && deviceTags.contains(tag))
            || (target == CloudPushService.ALIAS_TARGET && aliasTags.contains(tag))
            || (target == CloudPushService.ACCOUNT_TARGET && accountTags.contains(tag))
        ) {
            getApplication<MainApplication>().toast(
                R.string.push_already_add
            )
        }
        PushServiceFactory.getCloudPushService()
            .bindTag(target, arrayOf(tag), alias, object : CommonCallback {
                override fun onSuccess(p0: String?) {
                    hasTag.value = true
                    when (target) {
                        CloudPushService.DEVICE_TARGET -> addDeviceTagSuccess(tag)
                        CloudPushService.ALIAS_TARGET -> addAliasTagSuccess(tag, alias)
                        CloudPushService.ACCOUNT_TARGET -> addAccountTagSuccess(tag)
                    }
                }

                override fun onFailed(errorCode: String?, errorMessage: String?) {
                    getApplication<MainApplication>().toast(
                        R.string.push_toast_add_tag_fail,
                        errorMessage
                    )
                }

            })
    }

    private fun addDeviceTagSuccess(tag: String) {
        deviceTags.add(0, tag)
        updateTagStatus()
        deviceTagData.value = deviceTags.joinToString(",")
    }

    private fun addAliasTagSuccess(tag: String, alias: String?) {
        if (TextUtils.isEmpty(alias)) return
        aliasTags.add(0, tag)
        updateTagStatus()
        aliasTagData.value = aliasTags.joinToString(",")
        if (aliasTagMap.containsKey(alias)) {
            aliasTagMap[alias!!] = "${aliasTagMap[alias]},$tag"
        } else {
            aliasTagMap[alias!!] = tag
        }
        viewModelScope.launch {
            val editor = preferences.edit()
            editor.putString(SP_KEY_ALIAS_TAG, aliasTagData.value)
            editor.putString(SP_KEY_ALIAS_TAG_MAP, gson.toJson(aliasTagMap))
            editor.apply()
        }
    }

    private fun addAccountTagSuccess(tag: String) {
        accountTags.add(0, tag)
        updateTagStatus()
        accountTagData.value = accountTags.joinToString(",")
        viewModelScope.launch {
            val editor = preferences.edit()
            editor.putString(SP_KEY_ACCOUNT_TAG, accountTagData.value)
            editor.apply()
        }
    }

    fun removeDeviceTag(tag: String) {
        PushServiceFactory.getCloudPushService()
            .unbindTag(CloudPushService.DEVICE_TARGET, arrayOf(tag), null, object : CommonCallback {
                override fun onSuccess(p0: String?) {
                    removeDeviceTagSuccess(tag)
                }

                override fun onFailed(p0: String?, p1: String?) {

                }

            })
    }

    private fun removeDeviceTagSuccess(tag: String) {
        deviceTags.remove(tag)
        updateTagStatus()
        deviceTagData.value = deviceTags.joinToString(",")
    }

    fun removeAliasTag(tag: String) {
        for (entry in aliasTagMap) {
            if (entry.value.contains(tag)) {
                PushServiceFactory.getCloudPushService()
                    .unbindTag(CloudPushService.ALIAS_TARGET, arrayOf(tag), entry.key, object : CommonCallback {
                        override fun onSuccess(p0: String?) {
                            removeAliasTagSuccess(tag, entry.key)
                        }

                        override fun onFailed(p0: String?, p1: String?) {

                        }

                    })
                break
            }
        }
    }

    private fun removeAliasTagSuccess(tag: String, alias: String) {
        aliasTags.remove(tag)
        updateTagStatus()
        aliasTagData.value = aliasTags.joinToString(",")

        aliasTagMap[alias]?.split(",")?.let {
            val aliasTagList = it.toMutableList()
            aliasTagList.remove(tag)
            aliasTagMap[alias] = aliasTagList.joinToString(",")
        }

        viewModelScope.launch {
            val editor = preferences.edit()
            editor.putString(SP_KEY_ALIAS_TAG, aliasTagData.value)
            editor.putString(SP_KEY_ALIAS_TAG_MAP, gson.toJson(aliasTagMap))
            editor.apply()
        }


    }

    fun removeAccountTag(tag: String) {
        PushServiceFactory.getCloudPushService()
            .unbindTag(CloudPushService.ACCOUNT_TARGET, arrayOf(tag), null, object : CommonCallback {
                override fun onSuccess(p0: String?) {
                    removeAccountTagSuccess(tag)
                }

                override fun onFailed(p0: String?, p1: String?) {

                }

            })
    }

    private fun removeAccountTagSuccess(tag: String) {
        accountTags.remove(tag)
        updateTagStatus()
        accountTagData.value = accountTags.joinToString(",")
        viewModelScope.launch {
            val editor = preferences.edit()
            editor.putString(SP_KEY_ACCOUNT_TAG, accountTagData.value)
            editor.apply()
        }
    }

    private fun updateTagStatus(){
        hasTag.value = deviceTags.isNotEmpty() || aliasTags.isNotEmpty() || accountTags.isNotEmpty()
        hasDeviceTag.value = deviceTags.isNotEmpty()
        showMoreDeviceTag.value = deviceTags.size > showMoreTagCount
        showDividerDeviceTag.value = deviceTags.isNotEmpty() && (aliasTags.isNotEmpty() || accountTags.isNotEmpty())

        hasAliasTag.value = aliasTags.isNotEmpty()
        showMoreAliasTag.value = aliasTags.size > showMoreTagCount
        showDividerAliasTag.value = aliasTags.isNotEmpty() && accountTags.isNotEmpty()

        hasAccountTag.value = accountTags.isNotEmpty()
        showMoreAccountTag.value = accountTags.size > showMoreTagCount
    }

    fun alreadyAddAlias(alias: String): Boolean {
        return currAliasList.contains(alias)
    }

    private fun getString(res: Int): String {
        return getApplication<MainApplication>().getString(res)
    }

}