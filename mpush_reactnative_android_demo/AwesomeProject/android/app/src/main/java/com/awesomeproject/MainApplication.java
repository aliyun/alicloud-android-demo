package com.awesomeproject;

import android.app.Application;

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
    this.initCloudChannel();
  }

  private void initCloudChannel() {
    PushServiceFactory.init(this.getApplicationContext());
    PushServiceFactory.getCloudPushService().register(this.getApplicationContext(), new CommonCallback() {
      @Override
      public void onSuccess(String s) {
        WritableMap params = Arguments.createMap();
        params.putBoolean("success", true);
        PushModule.sendEvent("onInit", params);
      }

      @Override
      public void onFailed(String s, String s1) {
        WritableMap params = Arguments.createMap();
        params.putBoolean("success", false);
        params.putString("errorMsg", "errorCode:" + s + ". errorMsg:" + s1);
        PushModule.sendEvent("onInit", params);
      }
    });
  }
}
