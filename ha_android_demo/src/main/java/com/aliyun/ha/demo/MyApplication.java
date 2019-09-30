package com.aliyun.ha.demo;

import android.app.Application;
import android.util.Log;

import com.alibaba.ha.adapter.AliHaAdapter;
import com.alibaba.ha.adapter.AliHaConfig;
import com.alibaba.ha.adapter.Plugin;

public class MyApplication extends Application {
    private final static String mAppKey = "25027611";
    private final static String mAppSecret = "65acb9cca300ba4b9d807ebdb94ed323";
    private final static String mHARSAPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCT541JEnoi871DuM5L49SlxbgMko92HQ9lKv+KDF+4gw1jmCCU1kaFaRwCLwV384PgkGCWxvdf8fIl4xB6MgMS2zVaUtuTYI30fJ2Y6Hsgu0uO/huYoqqKBEhLko/kIaR7/QKI3dGe5mkj6/J0Pn9sFNb46Wa0o54Ebk4BJLKkOQIDAQAB";

    @Override
    public void onCreate() {
        super.onCreate();
        initHa();
    }

    private void initHa() {
        Log.e("ha", "init");

        //ha init
        AliHaConfig config = new AliHaConfig();
        config.appKey = mAppKey;
        config.appVersion = BuildConfig.VERSION_NAME;
        config.appSecret = mAppSecret;
        config.channel = "mqc_test";
        config.userNick = null;
        config.application = this;
        config.context = getApplicationContext();
        config.isAliyunos = false;
        config.rsaPublicKey = mHARSAPublicKey;

        AliHaAdapter.getInstance().addPlugin(Plugin.crashreporter);    //崩溃分析，如不需要可注释掉
        AliHaAdapter.getInstance().addPlugin(Plugin.apm);              //性能监控，如不需要可注释掉
        AliHaAdapter.getInstance().addPlugin(Plugin.tlog);             //移动日志，如不需要可注释掉

        AliHaAdapter.getInstance().openDebug(true);          //调试日志开关

        AliHaAdapter.getInstance().start(config);                     //启动
    }
}
