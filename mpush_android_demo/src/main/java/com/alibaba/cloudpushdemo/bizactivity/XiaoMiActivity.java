package com.alibaba.cloudpushdemo.bizactivity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.alibaba.cloudpushdemo.R;
import com.alibaba.sdk.android.push.AndroidPopupActivity;

import java.util.Map;

/**
 * 小米辅助弹窗指定打开Activity
 */
public class XiaoMiActivity extends AndroidPopupActivity {
    final String TAG = "XiaoMiActivity";

    TextView xiaomiLabel;

    /**
     * 用于其他Activty跳转到该Activity
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, XiaoMiActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiao_mi);
        // ActionBar 回退导航
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        xiaomiLabel = (TextView) this.findViewById(R.id.XiaoMiLabel);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String summary = intent.getStringExtra("summary");
        if (title != null && summary != null) {
            xiaomiLabel.setText("普通推送通道弹出");
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            xiaomiLabel.setText((String)msg.obj);
        }
    };

    /**
     * 小米辅助弹窗指定打开Activity回调
     * @param title     标题
     * @param content   内容
     * @param extraMap  额外参数
     */
//    @Override
//    protected void onMiPushSysNoticeOpened(String title, String content, Map<String, String> extraMap) {
//        Log.d(TAG, "Receive XiaoMi notification, title: " + title + ", content: " + content + ", extraMap: " + extraMap);
//        mHandler.sendMessage(mHandler.obtainMessage(0, "小米辅助弹窗通道打开"));
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSysNoticeOpened(String title, String content, Map<String, String> extraMap) {
        Log.d(TAG, "Receive XiaoMi notification, title: " + title + ", content: " + content + ", extraMap: " + extraMap);
        mHandler.sendMessage(mHandler.obtainMessage(0, "小米辅助弹窗通道打开"));
    }
}
