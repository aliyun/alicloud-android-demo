package com.alibaba.push.android.demo

import android.content.Context
import android.text.TextUtils
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

    fun removeLabel(labelType: Int, value: String){
        if (!data.containsKey(labelType) || !data[labelType]!!.contains(value)) {
            return
        }
        data[labelType]?.remove(value)
    }

    fun getLabels(labelType: Int): MutableList<String> {
        if (!data.containsKey(labelType)) {
            data[labelType] = mutableListOf()
        }
        return data[labelType]!!
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

    fun removeDeviceTag(context: Context, tag: String, callback:(String)->Unit) {
        PushServiceFactory.getCloudPushService().unbindTag(CloudPushService.DEVICE_TARGET , arrayOf(tag), null, object:
            CommonCallback {
            override fun onSuccess(p0: String?) {
                callback.invoke(tag)
                removeLabel(LABEL_DEVICE_TAG, tag)
            }

            override fun onFailed(erroCode: String?, errorMessage: String?) {
                context.toast(R.string.push_toast_delete_device_tag_fail, errorMessage)
            }

        })
    }

    fun addAlias(context: Context, callback:(String)->Unit) {
        context.showInputDialog(
            R.string.push_add_alias, R.string.push_input_alias_hint,
            showAlert = false,
            showAliasInput = false
        ) { it, _ ->
            addAlias(context, it, callback)
        }
    }

    private fun addAlias(context: Context, alias: String, callback:(String)->Unit) {
        PushServiceFactory.getCloudPushService().addAlias(alias, object : CommonCallback {
            override fun onSuccess(p0: String?) {
                callback.invoke(alias)
                addLabel(LABEL_ALIAS, alias)
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                context.toast(
                    String.format(
                        context.getString(R.string.push_toast_add_alias_fail),
                        errorMessage
                    )
                )
            }

        })
    }

    fun removeAlias(context: Context,alias: String, callback:(String)->Unit) {
        PushServiceFactory.getCloudPushService().removeAlias(alias, object : CommonCallback {
            override fun onSuccess(p0: String?) {
                callback.invoke(alias)
                removeLabel(LABEL_ALIAS, alias)
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

    fun addAliasTag(context: Context, callback: (String) -> Unit) {
        context.showInputDialog(
            R.string.push_add_alias_tag, R.string.push_input_alias_tag_hint,
            showAlert = true,
            showAliasInput = true
        ) { it, alias ->
            if (TextUtils.isEmpty(alias)) {
                context.toast(context.getString(R.string.push_alias_empty))
                return@showInputDialog
            }
            addAliasTag(context, alias!!, it, callback)
        }
    }

    private fun addAliasTag(context: Context, alias: String, tag: String, callback: (String) -> Unit) {
        PushServiceFactory.getCloudPushService()
            .bindTag(CloudPushService.ALIAS_TARGET, arrayOf(tag), alias, object : CommonCallback {
                override fun onSuccess(p0: String?) {
                    callback.invoke(tag)
                    aliasTagMap[tag] = alias
                    addLabel(LABEL_ALIAS_TAG, tag)
                }

                override fun onFailed(errorCode: String?, errorMessage: String?) {
                    context.toast(
                        String.format(
                            context.getString(R.string.push_toast_add_alias_tag_fail),
                            errorMessage
                        )
                    )
                }

            })
    }

    fun removeAliasTag(context: Context, tag: String, callback: (String) -> Unit) {
        PushServiceFactory.getCloudPushService().unbindTag(
            CloudPushService.ALIAS_TARGET,
            arrayOf(tag),
            aliasTagMap[tag],
            object : CommonCallback {
                override fun onSuccess(p0: String?) {
                    callback.invoke(tag)
                    removeLabel(LABEL_ALIAS_TAG, tag)
                    aliasTagMap.remove(tag)
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

    fun addAccountTag(context: Context, callback: (String) -> Unit) {
        context.showInputDialog(
            R.string.push_bind_account_tag, R.string.push_bind_account_tag_hint,
            showAlert = true,
            showAliasInput = false
        ) { it, _ ->
            addAccountTag(context, it, callback)
        }
    }

    private fun addAccountTag(context: Context, tag: String, callback: (String) -> Unit) {
        PushServiceFactory.getCloudPushService()
            .bindTag(CloudPushService.ACCOUNT_TARGET, arrayOf(tag), null, object : CommonCallback {
                override fun onSuccess(p0: String?) {
                    callback.invoke(tag)
                    addLabel(LABEL_ACCOUNT_TAG, tag)
                }

                override fun onFailed(errorCode: String?, errorMessage: String?) {
                    context.toast(R.string.push_bind_account_tag_fail, errorMessage)
                }

            })
    }

    fun removeAccountTag(context: Context, tag: String, callback: (String) -> Unit) {
        PushServiceFactory.getCloudPushService()
            .bindTag(CloudPushService.ACCOUNT_TARGET, arrayOf(tag), null, object : CommonCallback {
                override fun onSuccess(p0: String?) {
                    callback.invoke(tag)
                    removeLabel(LABEL_ACCOUNT_TAG, tag)
                }

                override fun onFailed(errorCode: String?, errorMessage: String?) {
                    context.toast(R.string.push_bind_account_tag_fail, errorMessage)
                }

            })
    }

}

