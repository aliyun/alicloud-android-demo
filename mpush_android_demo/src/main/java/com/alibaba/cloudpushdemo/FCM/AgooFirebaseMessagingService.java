package com.alibaba.cloudpushdemo.FCM;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.push.register.ThirdPushManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.taobao.accs.utl.ALog;

public class AgooFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TAG = "MPS:GcmRegister";

    public AgooFirebaseMessagingService() {
    }

    public void onCreate() {
        super.onCreate();
        ALog.d(TAG, "AgooFirebaseMessagingService oncreate", new Object[0]);
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            ALog.d(TAG, "onMessageReceived", new Object[0]);
            if (remoteMessage.getData() != null) {
                String msg = (String) remoteMessage.getData().get("payload");
                ALog.d(TAG, "onMessageReceived payload msg:" + msg, new Object[0]);
                if (!TextUtils.isEmpty(msg)) {
                    ThirdPushManager.onPushMsg(this.getApplicationContext(), ThirdPushManager.ThirdPushReportKeyword.FCM.thirdMsgKeyword, msg);
                }
            }
        } catch (Throwable var3) {
            ALog.e(TAG, "onMessageReceived", var3, new Object[0]);
        }

    }

    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }


    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM registration token with any
     * server-side account maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        ALog.w(TAG, "token:" + token);
        ThirdPushManager.reportToken(this, ThirdPushManager.ThirdPushReportKeyword.FCM.thirdTokenKeyword, token, "22.0.0");
    }
}