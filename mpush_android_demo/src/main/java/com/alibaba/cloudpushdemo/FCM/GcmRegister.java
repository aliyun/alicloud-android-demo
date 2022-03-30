package com.alibaba.cloudpushdemo.FCM;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.cloudpushdemo.bizactivity.MainActivity;
import com.alibaba.sdk.android.push.register.ThirdPushManager;
import com.alibaba.sdk.android.push.utils.SysUtils;
import com.alibaba.sdk.android.push.utils.ThreadUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseOptions.Builder;
import com.google.firebase.messaging.FirebaseMessaging;
import com.taobao.accs.utl.ALog;

public class GcmRegister {
    public static final String TAG = "MPS:GcmRegister";
    private static volatile boolean isRegistered = false;

    public GcmRegister() {
    }

    public static boolean register(final Context context, final String sendId, final String applicationId) {
        if (!SysUtils.isMainProcess(context)) {
            ALog.i(TAG, "not in main process, return", new Object[0]);
            return false;
        } else {
            ThreadUtil.getExecutor().execute(new Runnable() {
                public void run() {
                    try {
                        if (GcmRegister.isRegistered) {
                            ALog.w(TAG, "registered already", new Object[0]);
                            return;
                        }

                        GcmRegister.isRegistered = true;
                        Builder builder = new Builder();
                        builder.setGcmSenderId(sendId);
                        builder.setApplicationId(applicationId);
                        FirebaseOptions options = builder.build();

                        try {
                            FirebaseApp.initializeApp(context.getApplicationContext(), options);
                        } catch (Throwable var4) {
                            ALog.w(TAG, "register initializeApp", var4, new Object[0]);
                        }
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (!task.isSuccessful()) {
                                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                            return;
                                        }

                                        // Get new FCM registration token
                                        String token = task.getResult();
                                        Log.d(TAG, token);
                                        ALog.i(TAG, "register token：" + token, new Object[0]);
//                                        AgooFirebaseInstanceIDService.reportGcmToken(context, token);
                                        ThirdPushManager.reportToken(context, ThirdPushManager.ThirdPushReportKeyword.FCM.thirdTokenKeyword, token, "22.0.0");
                                    }
                                });
                    } catch (Throwable var5) {
                        ALog.e(TAG, "register", var5, new Object[0]);
                    }

                }
            });
            return true;
        }
    }
}
