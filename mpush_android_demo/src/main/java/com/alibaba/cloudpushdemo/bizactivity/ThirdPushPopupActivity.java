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
 * 辅助推送通道指定打开的弹窗activity,目前包括:小米弹窗、华为弹窗
 */
public class ThirdPushPopupActivity extends AndroidPopupActivity {
    final String TAG = "ThirdPushPopupActivity";

    TextView thirPushLabel;

    /**
     * 用于其他Activty跳转到该Activity
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ThirdPushPopupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_third_push);
        // ActionBar 回退导航
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        thirPushLabel = (TextView) this.findViewById(R.id.XiaoMiLabel);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String summary = intent.getStringExtra("summary");
        if (title != null && summary != null) {
            thirPushLabel.setText("普通推送通道弹出");
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            thirPushLabel.setText((String)msg.obj);
        }
    };


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

    /**
     * 弹窗消息打开互调。辅助弹窗通知被点击时,此回调会被调用,用户可以从该回调中获取相关参数进行下一步处理
     * @param title
     * @param content
     * @param extraMap
     */
    @Override
    protected void onSysNoticeOpened(String title, String content, Map<String, String> extraMap) {
        Log.d(TAG, "Receive ThirdPush notification, title: " + title + ", content: " + content + ", extraMap: " + extraMap);
        mHandler.sendMessage(mHandler.obtainMessage(0, "小米辅助弹窗通道打开"));
    }
}
