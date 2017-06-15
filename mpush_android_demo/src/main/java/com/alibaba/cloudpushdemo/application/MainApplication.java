package com.alibaba.cloudpushdemo.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.GcmRegister;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;

public class MainApplication extends Application {
    private static final String TAG = "Init";
    @Override
    public void onCreate() {
        super.onCreate();

//        l.b(this);
//        l.e(this);
//        l.c(this);
//        SharedPreferences sp = this.getSharedPreferences(Constants.SP_FILE_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.clear();
//        editor.apply();

        initCloudChannel(this);
    }

    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private void initCloudChannel(final Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.i(TAG, "init cloudchannel success");
                pushService.bindAccount("test", new CommonCallback() {
                    @Override
                    public void onSuccess(String s) {

                    }

                    @Override
                    public void onFailed(String s, String s1) {

                    }
                });

            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });

        MiPushRegister.register(applicationContext, "2882303761517410754", "5661741045754");
        HuaWeiRegister.register(applicationContext);
        GcmRegister.register(applicationContext, "1014340708672", "1:1014340708672:android:d078cef4f8b2542a");
    }

    private void startKeepChannelService(Context applicationContext) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            JobInfo.Builder builder = new JobInfo.Builder(900715, new ComponentName(applicationContext.getPackageName(), AccsJobService.class.getName()));
//            if (Build.VERSION.SDK_INT >= 24) {
//                builder.setMinimumLatency(270 * 1000);
//            } else {
//                builder.setPeriodic(270 * 1000);  //间隔500毫秒调用onStartJob函数， 500只是为了验证
//            }
//            PersistableBundle bundle = new PersistableBundle();
//            bundle.putString("keep", "enable");
//            builder.setExtras(bundle);
//            JobScheduler jobScheduler = (JobScheduler) applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//            int ret = jobScheduler.schedule(builder.build());
//            Log.d("haha","KeepChannelServiceId:" + ret);
//            if (ret <= 0) {
//                Log.e("haha","Start KeepChannelService failed. ErrorCode:" + ret);
//            }
//        }

    }
}
