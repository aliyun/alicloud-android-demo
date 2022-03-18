package com.awesomeproject;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.huawei.HuaWeiRegister;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.GcmRegister;
import com.alibaba.sdk.android.push.register.MeizuRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.alibaba.sdk.android.push.register.OppoRegister;
import com.alibaba.sdk.android.push.register.VivoRegister;
import com.awesomeproject.push.PushModule;
import com.awesomeproject.push.PushPackage;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new PushPackage()
            );
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, /* native exopackage */ false);

        PushServiceFactory.init(this);


        initNotificationChannel();
        initCloudChannel();
    }

    /**
     * 初始化通知渠道
     */
    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id。
            String id = "1";
            // 用户可以看到的通知渠道的名字。
            CharSequence name = "notification channel";
            // 用户可以看到的通知渠道的描述。
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性。
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果Android设备支持的话）。
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果Android设备支持的话）。
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            // 最后在notificationmanager中创建该通知渠道。
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    private boolean pushInit;

    /**
     * 注册通知
     */
    public void initCloudChannel() {
        File is_privacy = new File(ContextCompat.getDataDir(this).getAbsolutePath(), ContVar.P_FILE);
        if (!is_privacy.exists()) return;
        if (pushInit) return;
        pushInit = true;

        if (BuildConfig.DEBUG) {
            //仅适用于Debug包，正式包不需要此行
            PushServiceFactory.getCloudPushService().setLogLevel(CloudPushService.LOG_DEBUG);
        }
        PushServiceFactory.getCloudPushService().register(this.getApplicationContext(), new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                pushInit = true;
                WritableMap params = Arguments.createMap();
                params.putBoolean("success", true);
                PushModule.sendEvent("onInit", params);
                initCS();
            }

            @Override
            public void onFailed(String s, String s1) {
                pushInit = false;
                WritableMap params = Arguments.createMap();
                params.putBoolean("success", false);
                params.putString("errorMsg", "errorCode:" + s + ". errorMsg:" + s1);
                PushModule.sendEvent("onInit", params);
            }
        });
    }

    /**
     * 初始化厂商
     */
    private void initCS() {
        HuaWeiRegister.register(this);
        MiPushRegister.register(this, "xxxxxxxx", "xxxxxxxx"); // 初始化小米辅助推送
        MeizuRegister.register(this, "xxxxxxxx", "");//接入魅族辅助推送
        OppoRegister.register(this, "xxxxxxxx", "");
        VivoRegister.register(this);//接入vivo辅助推送
        GcmRegister.register(this, "xxxxxxxx", "xxxxxxxx"); // 接入FCM/GCM初始化推送
    }

}
