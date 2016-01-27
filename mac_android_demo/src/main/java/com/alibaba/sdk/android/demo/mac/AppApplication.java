package com.alibaba.sdk.android.demo.mac;

import android.app.Application;
import android.util.Log;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.callback.InitResultCallback;
import com.alibaba.sdk.android.mac.MACService;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhouzhuo on 10/9/15.
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化OneSDK
        AlibabaSDK.asyncInit(getApplicationContext(), new InitResultCallback() {
            @Override
            public void onFailure(int arg0, String arg1) {
                Log.e("[oncreate]", "[ayncInit] - alibabaSDK init failed: " + arg0 + " " + arg1);
            }

            @Override
            public void onSuccess() {
                Log.d("[onCreate]", "[ayncInit] - alibabaSDK init success");
            }
        });

        // 初始化MAC，必须在OneSDK初始化之后
        MACService macService = AlibabaSDK.getService(MACService.class);
        macService.setApplicationContext(getApplicationContext());
        // macService.disableAutoDegrade();  // 如果是在接入调试阶段，调用这个接口关闭自动降级，确保配置正确
        macService.enableInWIFIMode(); // WIFI下依旧开启云加速模式
        macService.setConnectTimeout(15, TimeUnit.SECONDS); // 设置全局单次连接超时值为15秒
        macService.setReadTimeout(15, TimeUnit.SECONDS); // 设置全局单次读超时值为15秒
    }
}
