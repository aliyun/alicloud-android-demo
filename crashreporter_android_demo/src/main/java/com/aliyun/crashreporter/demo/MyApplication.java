package com.aliyun.crashreporter.demo;

import android.app.Application;
import android.util.Log;

import com.alibaba.ha.adapter.AliHaAdapter;
import com.alibaba.ha.adapter.AliHaConfig;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initHa();
    }

    private void initHa() {
        Log.e("ha", "init");

        //ha init
        AliHaAdapter.getInstance().openPublishEmasHa();

        AliHaConfig config = new AliHaConfig();
        config.appKey = "xxxx"; //
        config.appVersion = "2.0";
        config.appSecret = "xxxx";
        config.channel = "xxx";
        config.userNick = null;
        config.application = this;
        config.context = getApplicationContext();
        config.isAliyunos = false;

        AliHaAdapter.getInstance().startCrashReport(config);
    }
}
