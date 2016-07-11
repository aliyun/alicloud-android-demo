package com.alibaba.sdk.android.demo.mac;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.alibaba.sdk.android.mac.MACService;
import com.alibaba.sdk.android.mac.MACServiceProvider;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DemoActivity extends AppCompatActivity {

    private static final String TEST_URL = "http://macimg0bm.ams.aliyuncs.com/static_file/20k";
    private static final String TEST_POST_URL = "http://macapibm.ams.aliyuncs.com/mbaas/test";
    private static final String TAG = "MAC_DEMO";
    private static MACService macService;


    // 示例代码Section I
    // 本Section代码给出了集成MAC后基本网络操作的使用示例。事实上初始化MAC后对网络的实现代码与传统的Native库网络实现代码完全一致
    // 具体调用实现, 参见getRequest postRequest方法

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // 初始化移动加速服务
        macService = MACServiceProvider.getService(getApplicationContext());
        // 打开移动加速log, 仅供调试使用，正式上线请关闭log
        macService.setLogEnabled(true);

        Button getButton = (Button) this.findViewById(R.id.getButton);

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 网络请求请勿在UI线程执行
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getRequest();
                    }
                }).start();
            }
        });

        Button postButton = (Button) this.findViewById(R.id.postButton);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 网络请求请勿在UI线程执行
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        postRequest();
                    }
                }).start();
            }
        });
    }


    // GET请求示例
    public void getRequest() {
        try {
            HttpURLConnection conn = macService.open(new URL(TEST_URL));
            int responseCode = conn.getResponseCode();
            Log.d(TAG, "Response Code: " + responseCode);
            if (responseCode == 200) {
                InputStream in = conn.getInputStream();
                DataInputStream dis = new DataInputStream(in);
                int len = 0;
                byte[] buff = new byte[4096];
                while ((len = dis.read(buff)) != -1) {
                    Log.d(TAG, "Response: " + new String(buff, 0, len));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // POST请求示例
    public void postRequest() {
        try {
            byte[] buff = new byte[4096];
            InputStream in = null;
            OutputStream out = null;
            byte[] dataToPost = "This is some data for post request.".getBytes();
            HttpURLConnection conn = macService.open(new URL(TEST_POST_URL));
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            out = conn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            dos.write(dataToPost);
            int responseCode = conn.getResponseCode();
            Log.d(TAG, "Response Code: " + responseCode);
            if (responseCode == 200) {
                in = conn.getInputStream();
                DataInputStream dis = new DataInputStream(in);
                int len = 0;
                while ((len = dis.read(buff)) != -1) {
                    Log.d(TAG, "Response: " + new String(buff, 0, len));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
