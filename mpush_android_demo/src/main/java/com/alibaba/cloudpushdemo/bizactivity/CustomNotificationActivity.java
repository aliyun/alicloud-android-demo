package com.alibaba.cloudpushdemo.bizactivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.cloudpushdemo.R;
import com.alibaba.sdk.android.push.notification.AdvancedCustomPushNotification;
import com.alibaba.sdk.android.push.notification.BasicCustomPushNotification;
import com.alibaba.sdk.android.push.notification.CustomNotificationBuilder;

public class CustomNotificationActivity extends Activity {

    private Button btnBasicNotif, btnAdvancedNotif;

    /**
     * 用于其他Activty跳转到该Activity
     *
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, CustomNotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_notification);

        // ActionBar 回退导航
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.btnBasicNotif = (Button) findViewById(R.id.btn_setBasicCustomNotification);
        this.btnAdvancedNotif = (Button) findViewById(R.id.btn_setAdvancedCustomNotificaiton);

        this.btnBasicNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasicCustomPushNotification notification = new BasicCustomPushNotification();
                notification.setRemindType(BasicCustomPushNotification.REMIND_TYPE_SOUND);
                notification.setStatusBarDrawable(R.drawable.logo_yuanjiao_120);
                boolean res = CustomNotificationBuilder.getInstance().setCustomNotification(1, notification);
                Toast.makeText(CustomNotificationActivity.this, "Set Basic Notification:" + res + ", id:" + 1, Toast.LENGTH_SHORT).show();
            }
        });

        this.btnAdvancedNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdvancedCustomPushNotification notification = new AdvancedCustomPushNotification(R.layout.notification_layout, R.id.m_icon, R.id.m_title, R.id.m_text);
                notification.setServerOptionFirst(true);
                notification.setBuildWhenAppInForeground(false);
                boolean res = CustomNotificationBuilder.getInstance().setCustomNotification(2, notification);
                Toast.makeText(CustomNotificationActivity.this, "Set Advanced Notification:" + res + ", id:" + 2, Toast.LENGTH_SHORT).show();
            }
        });
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
}
