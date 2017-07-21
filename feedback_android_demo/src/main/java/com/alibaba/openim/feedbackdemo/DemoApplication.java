package com.alibaba.openim.feedbackdemo;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.mtl.appmonitor.AppMonitor;
import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.ErrorCode;
import com.alibaba.sdk.android.feedback.util.FeedbackErrorCallback;

import java.util.HashMap;
import java.util.concurrent.Callable;


public class DemoApplication extends Application {
    public final static String DEFAULT_APPKEY = "your appKey";
    public final static String DEFAULT_APPSECRET = "your appSecret";
    
    @Override
    public void onCreate() {
        super.onCreate();
        // 添加自定义的error handler
        FeedbackAPI.addErrorCallback(new FeedbackErrorCallback() {
            @Override
            public void onError(Context context, String errorMessage, ErrorCode code) {
                Toast.makeText(context, "ErrMsg is: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        FeedbackAPI.addLeaveCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                Log.d("DemoApplication", "custom leave callback");
                return null;
            }
        });

        //建议放在此处做初始化
        FeedbackAPI.init(this, DEFAULT_APPKEY, DEFAULT_APPSECRET);

        //设置默认联系方式
        FeedbackAPI.setDefaultUserContactInfo("13800000000");

        //沉浸式任务栏，控制台设置为true之后此方法才能生效
        FeedbackAPI.setTranslucent(true);

        //设置返回按钮图标
        FeedbackAPI.setBackIcon(R.drawable.ali_feedback_common_back_btn_bg);

        //设置标题栏"历史反馈"的字号，需要将控制台中此字号设置为0
        FeedbackAPI.setHistoryTextSize(20);


        // 验证代码
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("debug_api_url","http://muvp.alibaba-inc.com/online/UploadRecords.do");
        map.put("debug_key","aliyun_sdk_utDetection");
        //map.put("debug_sampling_option", "true");
        AppMonitor.turnOnRealTimeDebug(map);
    }
}
