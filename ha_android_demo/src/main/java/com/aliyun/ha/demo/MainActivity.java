package com.aliyun.ha.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.ha.adapter.AliHaAdapter;
import com.alibaba.ha.adapter.service.tlog.TLogService;
import com.alibaba.motu.tbrest.utils.DeviceUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import crashreporter.motu.alibaba.com.tbcrashreporter4androiddemo.NativeCrashTest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // java crash
        Button javaCrashBtn = (Button) findViewById(R.id.javaCrash);
        javaCrashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throw new NullPointerException();
            }
        });

        //native crash test
        final NativeCrashTest nativeCrashTest = new NativeCrashTest();
        Button nativeCrashBtn = (Button) findViewById(R.id.nativeCrash);
        nativeCrashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nativeCrashTest.TestNativeCrashMethod(1);
            }
        });

        //卡顿
        Button stuckCrashBtn = (Button) findViewById(R.id.stuck);
        stuckCrashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Thread.sleep(20*1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.customError).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AliHaAdapter.getInstance().reportCustomError(new NullPointerException());
            }
        });

        //打 tlog日志
        Button remoteDebugBtn = (Button) findViewById(R.id.logPrint);
        remoteDebugBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TAG = "HaDemo";
                String MODEL = "MainActivity";

                for (int i = 0; i < 50; ++i){
                    String uuid = getRandomNum();
                    TLogService.loge(MODEL,TAG,i+"" + uuid);
                    uuid = getRandomNum();
                    TLogService.loge(MODEL,TAG,"tlog test " + i+" hahh " + uuid);
                }

                Toast.makeText(MainActivity.this, "打日志完成，100条，model = " + MODEL + " tag = " + TAG,
                        Toast.LENGTH_SHORT).show();
            }
        });

        //主动上报日志
        Button logUploadBtn = (Button) findViewById(R.id.logUpload);
        logUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //上传
                startLog(getApplication().getApplicationContext());

                Toast.makeText(MainActivity.this, "主动上报今天的日志完成，当前设备ID为：" + DeviceUtils.getUtdid(getApplication().getApplicationContext()),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //上传
    public void startLog(final Context context){
        TimerTask task= new TimerTask() {
            @Override
            public void run() {
                TLogService.positiveUploadTlog(null);
            }
        };

        Timer timer = new Timer();
        //time为Date类型：在指定时间执行一次。
        timer.schedule(task, 3 * 1000);
    }

    //随机数
    private String getRandomNum(){
        try {
            UUID uuid = UUID.randomUUID();
            return uuid.toString().replace("-", "");
        }catch (Exception e){
            Log.w(AliHaAdapter.TAG,"get random num failure",e);
        }
        return null;
    }

}
