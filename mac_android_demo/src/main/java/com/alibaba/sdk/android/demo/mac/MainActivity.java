package com.alibaba.sdk.android.demo.mac;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.cas.CASException;
import com.alibaba.sdk.android.cas.CASService;
import com.alibaba.sdk.android.cas.spdu.SpduLog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SpduLog.enableLog(true);

        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < 5; i++) {

                    Log.v("Mainactivity", "[run] - start requesting via cas - ");

                    getRequest();

                    postRequest();

                    try {
                        Thread.sleep(1 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    public void getRequest() {
        CASService casService = AlibabaSDK.getService(CASService.class);
        byte[] buff = new byte[4096];
        String url = "http://m.taobao.com";

        try {
            HttpURLConnection conn = casService.open(new URL(url));
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream in = conn.getInputStream();
                DataInputStream dis = new DataInputStream(in);
                int len = 0;
                StringBuilder sb = new StringBuilder();
                while ((len = dis.read(buff)) != -1) {
                    sb.append(new String(buff, 0, len));
                }
                Log.d("Mainactivity", "[get] - " + sb.toString());
            }
        } catch (CASException e) {
            int errCode = e.getStatusCode();
            String errMsg = e.getMessage();
            String hostId = e.getHostId();
            e.printStackTrace();
        } catch (IOException e) {

        }
    }

    public void postRequest() {
        CASService casService = AlibabaSDK.getService(CASService.class);
        byte[] buff = new byte[4096];
        String url = "http://110.75.82.106/mbaas/test";
        InputStream in = null;
        OutputStream out = null;
        byte[] dataToPost = "This is some data to post.".getBytes();

        try {
            HttpURLConnection conn = casService.open(new URL(url));
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            out = conn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            dos.write(dataToPost);
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                in = conn.getInputStream();
                DataInputStream dis = new DataInputStream(in);
                int len = 0;
                while ((len = dis.read(buff)) != -1) {
                    System.out.println(new String(buff, 0, len));
                }
            }
        } catch (CASException e) {
            int errCode = e.getStatusCode();
            String errMsg = e.getMessage();
            String hostId = e.getHostId();
            e.printStackTrace();
        } catch (IOException e) {

        }
    }
}
