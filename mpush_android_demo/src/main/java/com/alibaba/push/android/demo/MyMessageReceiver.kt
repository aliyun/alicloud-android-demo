package com.alibaba.push.android.demo

import android.content.Context
import com.alibaba.sdk.android.push.MessageReceiver
import com.alibaba.sdk.android.push.notification.CPushMessage

/**
 * 通过广播接收下发的通知和消息
 * @author ren
 * @date 2024/09/20
 */
class MyMessageReceiver: MessageReceiver() {
    override fun onNotificationOpened(p0: Context?, p1: String?, p2: String?, p3: String?) {

    }

    override fun onNotificationRemoved(p0: Context?, p1: String?) {

    }

    override fun onNotification(
        p0: Context?,
        p1: String?,
        p2: String?,
        p3: MutableMap<String, String>?
    ) {

    }

    override fun onMessage(p0: Context?, p1: CPushMessage?) {

    }

    override fun onNotificationClickedWithNoAction(
        p0: Context?,
        p1: String?,
        p2: String?,
        p3: String?
    ) {

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

    }
}