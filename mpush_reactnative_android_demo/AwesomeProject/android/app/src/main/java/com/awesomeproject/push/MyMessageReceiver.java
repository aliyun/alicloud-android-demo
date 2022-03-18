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
        WritableMap params = Arguments.createMap();
        params.putString("content", s1);
        params.putString("title", s);
        PushModule.sendEvent("onNotificationOpened", params);
    }

    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        WritableMap params = Arguments.createMap();
        params.putString("title", title);
        params.putString("summary", summary);
        params.putString("extraMap", extraMap);
        PushModule.sendEvent("onNotificationClickedWithNoAction", params);
    }

    @Override
    protected void onNotificationRemoved(Context context, String s) {
        WritableMap params = Arguments.createMap();
        params.putString("title", s);
        PushModule.sendEvent("onNotificationRemoved", params);
    }

    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {

        WritableMap params = Arguments.createMap();
        params.putString("title", title);
        params.putString("summary", summary);
        for (String key : extraMap.keySet()) {
            params.putString("extraMap_" + key, extraMap.get(key));
        }
        params.putString("openType", openType + "");
        params.putString("openActivity", openActivity);
        params.putString("openUrl", openUrl);
        PushModule.sendEvent("onNotificationReceivedInApp", params);
    }

    @Override
    protected void onMessage(Context context, CPushMessage cPushMessage) {
        WritableMap params = Arguments.createMap();
        params.putString("messageId", cPushMessage.getMessageId());
        params.putString("content", cPushMessage.getContent());
        params.putString("title", cPushMessage.getTitle());
        PushModule.sendEvent("onMessage", params);
    }

    @Override
    protected void onNotification(Context context, String content, String title, Map<String, String> map) {
        WritableMap params = Arguments.createMap();
        params.putString("content", content);
        params.putString("title", title);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            params.putString(entry.getKey(), entry.getValue());
        }
        PushModule.sendEvent("onNotification", params);
    }

}
