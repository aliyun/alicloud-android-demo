package com.aliyun.ams.mac.demo;

import android.app.Application;
import android.content.Context;
import anet.channel.util.ALog;
import com.aliyun.ams.mac.api.MacClient;
import com.aliyun.ams.mac.api.MacConfig;

/**
 * Created by tomchen on 2017/7/3.
 */

public class DemoApplication extends Application {

    /**
     * 请配置appkey、appsecret
     */
    private static final String APP_KEY = "your appkey";

    private static final String APP_SECRET = "your appsecret";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ALog.setUseTlog(false);
        MacConfig config = new MacConfig.Builder()
                .context(this)
                .appKey(APP_KEY)
                .appSecret(APP_SECRET)
                .build();

        MacClient.init(config);
    }
}
