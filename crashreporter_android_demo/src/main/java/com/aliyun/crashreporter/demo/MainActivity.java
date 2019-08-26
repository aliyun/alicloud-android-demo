package com.aliyun.crashreporter.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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
        // native crash
        Button nativeCrashBtn = (Button) findViewById(R.id.nativeCrash);
        nativeCrashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nativeCrashTest.TestNativeCrashMethod(1);
            }
        });
    }

}
