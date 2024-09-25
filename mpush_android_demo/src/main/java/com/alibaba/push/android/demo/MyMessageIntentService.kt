package com.alibaba.push.android.demo

import android.content.Context
import com.alibaba.sdk.android.push.AliyunMessageIntentService
import com.alibaba.sdk.android.push.notification.CPushMessage

/**
 * 通过Service 处理下发通知和消息
 * @author ren
 * @date 2024/09/20
 */
class MyMessageIntentService: AliyunMessageIntentService() {
    override fun onNotificationOpened(p0: Context?, p1: String?, p2: String?, p3: String?) {
        TODO("Not yet implemented")
    }

    override fun onNotificationRemoved(p0: Context?, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun onNotification(
        p0: Context?,
        p1: String?,
        p2: String?,
        p3: MutableMap<String, String>?
    ) {
        TODO("Not yet implemented")
    }

    override fun onMessage(p0: Context?, p1: CPushMessage?) {
        TODO("Not yet implemented")
    }

    override fun onNotificationClickedWithNoAction(
        p0: Context?,
        p1: String?,
        p2: String?,
        p3: String?
    ) {
        TODO("Not yet implemented")
    }

    override fun onNotificationReceivedInApp(
        p0: Context?,
        p1: String?,
        p2: String?,
        p3: MutableMap<String, String>?,
        p4: Int,
        p5: String?,
        p6: String?
    ) {
        TODO("Not yet implemented")
    }
}