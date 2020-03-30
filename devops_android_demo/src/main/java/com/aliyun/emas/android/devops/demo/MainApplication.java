package com.aliyun.emas.android.devops.demo;

import android.app.Application;

import com.taobao.update.adapter.UpdateAdapter;
import com.taobao.update.apk.ApkUpdater;
import com.taobao.update.common.Config;
import com.taobao.update.common.framework.UpdateRuntime;
import com.taobao.update.datasource.UpdateDataSource;

public class MainApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Utils.initConfig(this);

        initUpdate();
    }

    private void initUpdate() {
        Config config = new Config();
        config.group = Utils.sAppkey + "@android";//AppInfoHelper.getGroup();
        config.ttid = Utils.sChannelID;
        config.isOutApk = false;
        config.appName = "EMAS Demo";

        UpdateRuntime.init(this, config.ttid, config.appName, config.group);
        ApkUpdater apkupdate = new ApkUpdater(getApplicationContext(), Utils.sAppkey, Utils.sAppSecret, config.group, Utils.sChannelID, config.ttid);
        UpdateAdapter updateAdapter = new UpdateAdapter();
        UpdateDataSource.getInstance().init(this, config.group, config.ttid, config.isOutApk, Utils.sAppkey, Utils.sAppSecret, Utils.sChannelID, updateAdapter);

        UpdateDataSource.getInstance().startUpdate(false);
    }
}
