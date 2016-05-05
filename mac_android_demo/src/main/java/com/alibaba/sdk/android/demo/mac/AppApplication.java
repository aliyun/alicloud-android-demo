package com.alibaba.sdk.android.demo.mac;

import android.app.Application;
import android.util.Log;

import com.alibaba.sdk.android.mac.MACService;
import com.alibaba.sdk.android.mac.MACServiceProvider;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhouzhuo on 10/9/15.
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化移动加速服务
        MACService macService = MACServiceProvider.getService(getApplicationContext());
        // 调试使用，正式上线请关闭log
        macService.setLogEnabled(true);
    }
}
