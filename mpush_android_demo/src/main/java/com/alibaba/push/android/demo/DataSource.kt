package com.alibaba.push.android.demo

import android.content.Context
import com.alibaba.sdk.android.push.CloudPushService
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory

object DataSource {
    const val LABEL_DEVICE_TAG = 0

    const val LABEL_ALIAS = 1

    const val LABEL_ALIAS_TAG = 2

    const val LABEL_ACCOUNT_TAG = 3

    private val data = mutableMapOf<Int, MutableList<String>>()

    private val aliasTagMap = mutableMapOf<String, String>()

    fun addLabel(labelType: Int, value: String) {
        if (!data.containsKey(labelType)) {
            data[labelType] = mutableListOf()
        }
        data[labelType]?.add(0, value)
    }

    fun getLabels(labelType: Int): MutableList<String> {
        if (!data.containsKey(labelType)) {
            data[labelType] = mutableListOf()
        }
        return data[labelType]!!
    }

    fun addAliasTag(alias: String, tag: String) {
        aliasTagMap[tag] = alias
    }

    fun getTagOfAlias(tag: String): String {
        return aliasTagMap[tag]!!
    }

    fun removeAliasTag(tag: String) {
        aliasTagMap.remove(tag)
    }

    fun addDeviceTag(context: Context,callback:(String)->Unit) {
        context.showInputDialog(R.string.push_add_device_tag, R.string.push_input_device_tag_hint,
            showAlert = true,
            showAliasInput = false) {it,_ ->
            addDeviceTag(context, it, callback)
        }
    }

    private fun addDeviceTag(context: Context, tag:String,callback:(String)->Unit) {
        PushServiceFactory.getCloudPushService().bindTag(CloudPushService.DEVICE_TARGET , arrayOf(tag), null, object:CommonCallback{
            override fun onSuccess(p0: String?) {
                callback.invoke(tag)
                addLabel(LABEL_DEVICE_TAG, tag)
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                context.toast(R.string.push_toast_add_device_tag_fail, errorMessage)
            }

        })
    }

    fun deleteDeviceTag(context: Context, tag: String, callback:(String)->Unit) {
        PushServiceFactory.getCloudPushService().unbindTag(CloudPushService.DEVICE_TARGET , arrayOf(tag), null, object:
            CommonCallback {
            override fun onSuccess(p0: String?) {
                callback.invoke(tag)
                data[LABEL_DEVICE_TAG]?.remove(tag)
            }

            override fun onFailed(erroCode: String?, errorMessage: String?) {
                context.toast(R.string.push_toast_delete_device_tag_fail, errorMessage)
            }

        })
    }

    fun deleteAlias(context: Context,alias: String, callback:(String)->Unit) {
        PushServiceFactory.getCloudPushService().removeAlias(alias, object : CommonCallback {
            override fun onSuccess(p0: String?) {
                callback.invoke(alias)
                data[LABEL_ALIAS]?.remove(alias)
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                context.toast(
                    String.format(
                        context.getString(R.string.push_toast_delete_alias_fail),
                        errorMessage
                    )
                )
            }
        })
    }

    fun deleteAliasTag(context: Context, tag: String, callback: (String) -> Unit) {
        PushServiceFactory.getCloudPushService().unbindTag(
            CloudPushService.ALIAS_TARGET,
            arrayOf(tag),
            getTagOfAlias(tag),
            object : CommonCallback {
                override fun onSuccess(p0: String?) {
                    callback.invoke(tag)
                    data[LABEL_ALIAS_TAG]?.remove(tag)
                    removeAliasTag(tag)
                }

                override fun onFailed(errorCode: String?, errorMessage: String?) {
                    context.toast(
                        String.format(
                            context.getString(R.string.push_toast_delete_alias_tag_fail),
                            errorMessage
                        )
                    )
                }
            })
    }

    fun deleteAccountTag(context: Context, tag: String, callback: (String) -> Unit) {
        PushServiceFactory.getCloudPushService()
            .bindTag(CloudPushService.ACCOUNT_TARGET, arrayOf(tag), null, object : CommonCallback {
                override fun onSuccess(p0: String?) {
                    callback.invoke(tag)
                    data[LABEL_ACCOUNT_TAG]?.remove(tag)
                }

                override fun onFailed(errorCode: String?, errorMessage: String?) {
                    context.toast(R.string.push_bind_account_tag_fail, errorMessage)
                }

            })
    }

}

