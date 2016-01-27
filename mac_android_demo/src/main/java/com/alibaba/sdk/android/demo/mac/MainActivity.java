package com.alibaba.sdk.android.demo.mac;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.mac.MACException;
import com.alibaba.sdk.android.mac.MACService;
import com.alibaba.sdk.android.mac.spdu.SpduLog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {

    private static final String TEST_URL = "http://macbm.ams.aliyuncs.com/static_file/20k";
    private static final String TEST_POST_URL = "http://macbm.ams.aliyuncs.com/mbaas/test";

    private Button btnBenchmarkStartMAC, btnBenchmarkStartNative;
    private TextView textViewMAC, textViewNative;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBenchmarkStartMAC = (Button) this.findViewById(R.id.btnMAC);
        btnBenchmarkStartNative = (Button) this.findViewById(R.id.btnNative);
        textViewMAC = (TextView) this.findViewById(R.id.rtMAC);
        textViewNative = (TextView) this.findViewById(R.id.rtNative);
        bar = (ProgressBar) this.findViewById(R.id.progressBar);
        bar.setEnabled(false);
        btnBenchmarkStartMAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasicTask basicTask = new BasicTask(btnBenchmarkStartMAC, btnBenchmarkStartNative, textViewMAC, bar, true);
                basicTask.execute();
            }
        });

        btnBenchmarkStartNative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasicTask basicTask = new BasicTask(btnBenchmarkStartMAC, btnBenchmarkStartNative, textViewNative, bar, false);
                basicTask.execute();
            }
        });

        btnBenchmarkStartMAC.setEnabled(true);
        btnBenchmarkStartNative.setEnabled(true);

        SpduLog.enableLog(false);

    }

    public void getRequest() {
        MACService macService = AlibabaSDK.getService(MACService.class);
        byte[] buff = new byte[4096];
        String url = TEST_URL;

        try {
            HttpURLConnection conn = macService.open(new URL(url));
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
        } catch (MACException e) {
            int errCode = e.getStatusCode();
            String errMsg = e.getMessage();
            String hostId = e.getHostId();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void postRequest() {
        MACService macService = AlibabaSDK.getService(MACService.class);
        byte[] buff = new byte[4096];
        String url = TEST_POST_URL;
        InputStream in = null;
        OutputStream out = null;
        byte[] dataToPost = "This is some data to post.".getBytes();

        try {
            HttpURLConnection conn = macService.open(new URL(url));
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
        } catch (MACException e) {
            int errCode = e.getStatusCode();
            String errMsg = e.getMessage();
            String hostId = e.getHostId();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
