package com.alibaba.sdk.android.demo.mac;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BenchmarkActivity extends AppCompatActivity {

    private Button btnBenchmarkStartMAC, btnBenchmarkStartNative;
    private TextView textViewMAC, textViewNative;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benchmark);

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


        BasicTask.setContext(getApplicationContext());

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

    }
}
