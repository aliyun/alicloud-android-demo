package com.awesomeproject;

import android.app.Application;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;

import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
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

        initCloudChannel();
    }

    private boolean pushInit;

    public void initCloudChannel() {
        File is_privacy = new File(ContextCompat.getDataDir(this).getAbsolutePath(), "emas_is_privacy");
        if (!is_privacy.exists()) return;
        if (pushInit) return;
        pushInit = true;

        PushServiceFactory.getCloudPushService().register(this.getApplicationContext(), new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                pushInit = true;
                WritableMap params = Arguments.createMap();
                params.putBoolean("success", true);
                PushModule.sendEvent("onInit", params);
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
}
