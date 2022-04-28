package com.alibaba.cloudpushdemo.application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.alibaba.cloudpushdemo.FCM.GcmRegister;
import com.alibaba.cloudpushdemo.bizactivity.MainActivity;
import com.alibaba.cloudpushdemo.component.MyMessageIntentService;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.huawei.HuaWeiRegister;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.MeizuRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.alibaba.sdk.android.push.register.OppoRegister;
import com.alibaba.sdk.android.push.register.VivoRegister;

public class MainApplication extends Application {
    private static final String TAG = "Init";
    private static MainActivity mainActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        initCloudChannel(this);
    }

    /**
     * 初始化云推送通道
     *
     * @param applicationContext
     */
    private void initCloudChannel(final Context applicationContext) {
        // 创建notificaiton channel
        this.createNotificationChannel();
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.setLogLevel(CloudPushService.LOG_DEBUG);
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.i(TAG, "init cloudchannel success");
                setConsoleText("init cloudchannel success");
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
                setConsoleText("init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
        HuaWeiRegister.register(this);
        MiPushRegister.register(applicationContext, "xiaomiId", "xiaomiKey"); // 初始化小米辅助推送
        MeizuRegister.register(applicationContext, "appid", "appKey");//接入魅族辅助推送
        OppoRegister.register(applicationContext, "appkey", "appSecret");
        VivoRegister.register(applicationContext);//接入vivo辅助推送
        GcmRegister.register(applicationContext, "sendId", "applicationId"); // 接入FCM/GCM初始化推送
    }

    public static void setMainActivity(MainActivity activity) {
        mainActivity = activity;
    }

    public static void setConsoleText(String text) {
        if (mainActivity != null && text != null) {
            mainActivity.appendConsoleText(text);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = "1";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "notification channel";
            // 用户可以看到的通知渠道的描述
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }
}
