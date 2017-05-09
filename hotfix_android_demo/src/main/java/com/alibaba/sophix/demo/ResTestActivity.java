package com.alibaba.sophix.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ResTestActivity extends AppCompatActivity {
    private TextView tv;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restest);

        tv = (TextView) findViewById(R.id.tv);
        tv.setText(R.string.tv_value);
        tv.setTextColor(getResources().getColor(R.color.colorBlack));

        iv = (ImageView) findViewById(R.id.iv);
        iv.setImageResource(R.mipmap.add);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(R.string.res_test);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv:
                break;
            case R.id.btn:
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
