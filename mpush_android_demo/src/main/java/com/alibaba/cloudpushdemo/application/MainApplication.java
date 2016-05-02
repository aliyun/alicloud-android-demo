package com.alibaba.cloudpushdemo.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.alibaba.cloudpushdemo.R;
import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.Environment;
import com.alibaba.sdk.android.callback.InitResultCallback;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.CloudPushService;


/**
 * @author: 正纬
 * @since: 15/4/16
 * @version: 1.0
 * @feature: 用于初始化SDK
 */
public class MainApplication extends Application {
    private static final String TAG = "Init";
    @Override
    public void onCreate() {

        super.onCreate();
        initOneSDK(this);
    }


    /**
     * 初始化AlibabaSDK
     * @param applicationContext
     */
    private void initOneSDK(final Context applicationContext) {
        AlibabaSDK.setEnvironment(Environment.ONLINE);
        AlibabaSDK.asyncInit(applicationContext, new InitResultCallback() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "init onesdk success");
                //alibabaSDK初始化成功后，初始化云推送通道
                initCloudChannel(applicationContext);

            }

            @Override
            public void onFailure(int code, String message) {
                Log.e(TAG, "init onesdk failed : " + message);
            }
        });
    }

    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        CloudPushService pushService = AlibabaSDK.getService(CloudPushService.class);
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "init cloudchannel success");
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.d(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });

    }
}
