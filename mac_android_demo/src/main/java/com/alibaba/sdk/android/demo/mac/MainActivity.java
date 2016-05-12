package com.alibaba.sdk.android.demo.mac;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.sdk.android.mac.MACService;
import com.alibaba.sdk.android.mac.MACServiceProvider;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {

    private static MACService macService;
    private static final String TEST_URL = "http://macbm.ams.aliyuncs.com/mac/test?expected=echo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        macService = MACServiceProvider.getService(getApplicationContext());

        // 本demo仅给出基本网络操作的使用示例。事实上初始化MAC后对网络的操作兼容HttpURLConnection库网络操作。
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 首请求用于SDK自适应学习加速域名，会走HttpURLConnection网络库逻辑
                // 如果您在初始化时通过presetMACDomains对移动加速域名进行了预热，则在预热结束后本次请求会直接走在移动加速链路上
                getRequest();
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 第二个请求开始会走移动加速逻辑
                getRequest();
                postRequest();
            }
        }).start();
    }

    // GET请求示例
    public void getRequest() {
        try {
            HttpURLConnection conn = macService.open(new URL(TEST_URL));
            int responseCode = conn.getResponseCode();
            Log.d("[MAC Demo]", "Response Code: " + responseCode);
            if (responseCode == 200) {
                InputStream in = conn.getInputStream();
                DataInputStream dis = new DataInputStream(in);
                int len = 0;
                byte[] buff = new byte[4096];
                while ((len = dis.read(buff)) != -1) {
                    Log.d("[MAC Demo]", "Response: " + new String(buff, 0, len));
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
            HttpURLConnection conn = macService.open(new URL(TEST_URL));
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            out = conn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            dos.write(dataToPost);
            int responseCode = conn.getResponseCode();
            Log.d("[MAC Demo]", "Response Code: " + responseCode);
            if (responseCode == 200) {
                in = conn.getInputStream();
                DataInputStream dis = new DataInputStream(in);
                int len = 0;
                while ((len = dis.read(buff)) != -1) {
                    Log.d("[MAC Demo]", "Response: " + new String(buff, 0, len));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
