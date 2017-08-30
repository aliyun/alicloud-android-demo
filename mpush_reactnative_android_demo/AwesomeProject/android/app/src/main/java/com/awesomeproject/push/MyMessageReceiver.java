package com.awesomeproject.push;

import android.content.Context;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import java.util.Map;

/**
 * Created by liyazhou on 17/5/18.
 */

public class MyMessageReceiver extends MessageReceiver {
    public MyMessageReceiver() {
        super();
    }

    @Override
    protected void onNotificationOpened(Context context, String s, String s1, String s2) {
        super.onNotificationOpened(context, s, s1, s2);
        WritableMap params = Arguments.createMap();
        params.putString("content", s1);
        params.putString("title", s);
        PushModule.sendEvent("onNotificationOpened", params);
    }

    @Override
    protected void onNotificationRemoved(Context context, String s) {
        super.onNotificationRemoved(context, s);
        WritableMap params = Arguments.createMap();
        params.putString("title", s);
        PushModule.sendEvent("onNotificationRemoved", params);
    }

    @Override
    protected void onMessage(Context context, CPushMessage cPushMessage) {
        super.onMessage(context, cPushMessage);
        WritableMap params = Arguments.createMap();
        params.putString("messageId", cPushMessage.getMessageId());
        params.putString("content", cPushMessage.getContent());
        params.putString("title", cPushMessage.getTitle());
        PushModule.sendEvent("onMessage", params);

    }

    @Override
    protected void onNotification(Context context, String s, String s1, Map<String, String> map) {
        super.onNotification(context, s, s1, map);
        WritableMap params = Arguments.createMap();
        params.putString("content", s1);
        params.putString("title", s);
        for (Map.Entry<String, String> entry: map.entrySet()) {
            params.putString(entry.getKey(), entry.getValue());
        }
        PushModule.sendEvent("onNotification", params);
    }

}
