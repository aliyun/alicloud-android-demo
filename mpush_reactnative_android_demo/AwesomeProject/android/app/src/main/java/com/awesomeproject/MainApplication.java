package com.awesomeproject;

import android.app.Application;
import android.util.Log;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {
  private static final String TAG = MainApplication.class.getName();

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    public boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage()
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
    CloudPushService pushService = PushServiceFactory.getCloudPushService();
    pushService.register(this.getApplicationContext(), "23552881", "c39da303685f4e88ebdcdf49b7fd6472", new CommonCallback() {
      @Override
      public void onSuccess(String s) {
        Log.e(TAG, "init cloudchannel success");
      }

      @Override
      public void onFailed(String s, String s1) {
        Log.e(TAG, "init cloudchannel failed. errorCode:" + s +  ". errorMsg:" + s1);
      }
    });
  }
}
