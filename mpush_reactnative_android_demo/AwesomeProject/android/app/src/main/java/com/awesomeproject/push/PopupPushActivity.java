package com.awesomeproject.push;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.alibaba.sdk.android.push.AndroidPopupActivity;
import com.awesomeproject.MainActivity;
import com.awesomeproject.MainApplication;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import java.util.Map;

public class PopupPushActivity extends AndroidPopupActivity {
    @Override
    protected void onSysNoticeOpened(String title, String summary, Map<String, String> extraMap) {
        Log.d("AIXI_Push", String.format("title:%s summary:%s ext:%s", title, summary, extraMap.toString()));
        WritableMap params = Arguments.createMap();
        params.putString("title", title);
        params.putString("summary", summary);
        for (String key : extraMap.keySet()) {
            params.putString("extraMap_" + key, extraMap.get(key));
        }
        new Thread(() -> {
            SystemClock.sleep(3000);
            PushModule.sendEvent("onSysNoticeOpened", params);
        }).start();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
