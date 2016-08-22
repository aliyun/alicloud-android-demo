package com.alibaba.cloudpushdemo.bizactivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.alibaba.cloudpushdemo.R;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;

public class SettingNoticeActivity extends Activity implements View.OnClickListener {

    final String SETTING_NOTICE = "setting_notification";

    final String DEFAULT_RES_PATH_FREFIX = "android.resource://";
    final String DEFAULT_RES_SOUND_TYPE = "raw";
    final String DEFAULT_RES_ICON_TYPE = "drawable";

    final String DEFAULT_NOTICE_SOUND = "alicloud_notification_sound";
    final String ASSIGN_NOTIFCE_SOUND = "alicloud_notification_sound_assign";
    final String DEFAULT_NOTICE_LARGE_ICON = "alicloud_notification_largeicon";
    final String ASSIGN_NOTIFCE_LARGE_ICON = "alicloud_notification_largeicon_assign";
    final String DEFAULT_NOTICE_SMALL_ICON = "alicloud_notification_smallicon";
    final String ASSIGN_NOTICE_SMALL_ICON = "alicloud_notification_smallicon_assign";

    String PackageName;

    Button defaultSoundPathBtn;
    Button setSoundPathBtn;
    Button defaultLargeIconIdBtn;
    Button setLargeIconIdBtn;
    Button defaultSmallIconIdBtn;
    Button setSmallIconIdBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notice);

        PackageName = getPackageName();

        // ActionBar 回退导航
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        defaultSoundPathBtn = (Button)this.findViewById(R.id.default_sound);
        setSoundPathBtn = (Button)this.findViewById(R.id.assign_sound);
        defaultLargeIconIdBtn = (Button)this.findViewById(R.id.default_largeicon);
        setLargeIconIdBtn = (Button)this.findViewById(R.id.assign_largeicon);
        defaultSmallIconIdBtn = (Button)this.findViewById(R.id.default_smallicon);
        setSmallIconIdBtn = (Button)this.findViewById(R.id.assign_smallicon);

        defaultSoundPathBtn.setOnClickListener(this);
        setSoundPathBtn.setOnClickListener(this);
        defaultLargeIconIdBtn.setOnClickListener(this);
        setLargeIconIdBtn.setOnClickListener(this);
        defaultSmallIconIdBtn.setOnClickListener(this);
        setSmallIconIdBtn.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            // 默认声音文件
            case R.id.default_sound:
                /**
                 * 如果用户未调用过setNotificationSoundFilePath()接口，默认取R.raw.alicloud_notification_sound声音文件
                 * 则无需使用以下方式进行设置
                 */
                int defaultSoundId = getResources().getIdentifier(DEFAULT_NOTICE_SOUND, DEFAULT_RES_SOUND_TYPE, PackageName);
                if (defaultSoundId != 0) {
                    String defaultSoundPath = DEFAULT_RES_PATH_FREFIX + getPackageName() + "/" + defaultSoundId;
                    PushServiceFactory.getCloudPushService().setNotificationSoundFilePath(defaultSoundPath);
                    Log.i(SETTING_NOTICE, "Set notification sound res id to R." + DEFAULT_RES_SOUND_TYPE + "." + DEFAULT_NOTICE_SOUND);
                } else {
                    Log.e(SETTING_NOTICE, "Set notification sound path error, R."
                            + DEFAULT_RES_SOUND_TYPE + "." + DEFAULT_NOTICE_SOUND + " not found.");
                }
                break;
            // 指定声音文件路径
            case R.id.assign_sound:
                /**
                 * 此处指定资源Id为R.raw.alicloud_notification_sound_assgin的声音文件
                 */
                int assignSoundId = getResources().getIdentifier(ASSIGN_NOTIFCE_SOUND, DEFAULT_RES_SOUND_TYPE, PackageName);
                if (assignSoundId != 0) {
                    String defaultSoundPath = DEFAULT_RES_PATH_FREFIX + getPackageName() + "/" + assignSoundId;
                    PushServiceFactory.getCloudPushService().setNotificationSoundFilePath(defaultSoundPath);
                    Log.i(SETTING_NOTICE, "Set notification sound res id to R." + DEFAULT_RES_SOUND_TYPE + "." + ASSIGN_NOTIFCE_SOUND);
                } else {
                    Log.e(SETTING_NOTICE, "Set notification sound path error, R."
                            + DEFAULT_RES_SOUND_TYPE + "." + ASSIGN_NOTIFCE_SOUND + " not found.");
                }
                break;
            // 默认通知栏图标资源
            case R.id.default_largeicon:
                /**
                 * 如果用户未调用过setNotificationLargeIcon()接口，默认取R.raw.alicloud_notification_largeicon图标资源
                 * 则无需使用以下方式进行设置
                 */
                int defaultLargeIconId = getResources().getIdentifier(DEFAULT_NOTICE_LARGE_ICON, DEFAULT_RES_ICON_TYPE, PackageName);
                if (defaultLargeIconId != 0) {
                    Drawable drawable = getApplicationContext().getResources().getDrawable(defaultLargeIconId);
                    if (drawable != null) {
                        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                        PushServiceFactory.getCloudPushService().setNotificationLargeIcon(bitmap);
                        Log.i(SETTING_NOTICE, "Set notification largeIcon res id to R." + DEFAULT_RES_ICON_TYPE + "." + DEFAULT_NOTICE_LARGE_ICON);
                    }
                } else {
                    Log.e(SETTING_NOTICE, "Set largeIcon bitmap error, R."
                            + DEFAULT_RES_ICON_TYPE + "." + DEFAULT_NOTICE_LARGE_ICON + " not found.");
                }
                break;
            // 指定通知栏图标文件
            case R.id.assign_largeicon:
                /**
                 * 此处指定资源Id为R.raw.alicloud_notification_largeicon_assgin的图标资源
                 */
                int assignLargeIconId = getResources().getIdentifier(ASSIGN_NOTIFCE_LARGE_ICON, DEFAULT_RES_ICON_TYPE, PackageName);
                if (assignLargeIconId != 0) {
                    Drawable drawable = getApplicationContext().getResources().getDrawable(assignLargeIconId);
                    if (drawable != null) {
                        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                        PushServiceFactory.getCloudPushService().setNotificationLargeIcon(bitmap);
                        Log.i(SETTING_NOTICE, "Set notification largeIcon res id to R." + DEFAULT_RES_ICON_TYPE + "." + ASSIGN_NOTIFCE_LARGE_ICON);
                    }
                } else {
                    Log.e(SETTING_NOTICE, "Set largeIcon bitmap error, R."
                            + DEFAULT_RES_ICON_TYPE + "." + ASSIGN_NOTIFCE_LARGE_ICON + " not found.");
                }
                break;
            // 默认状态栏图标资源Id
            case R.id.default_smallicon:
                /**
                 * 如果用户未调用过setNotificationSmallIcon()接口，默认取R.raw.alicloud_notification_smallicon图标资源
                 * 则无需使用以下方式进行设置
                 */
                int defaultSmallIconId = getResources().getIdentifier(DEFAULT_NOTICE_LARGE_ICON, DEFAULT_RES_ICON_TYPE, PackageName);
                if (defaultSmallIconId != 0) {
                    PushServiceFactory.getCloudPushService().setNotificationSmallIcon(defaultSmallIconId);
                    Log.i(SETTING_NOTICE, "Set notification smallIcon res id to R." + DEFAULT_RES_ICON_TYPE + "." + DEFAULT_NOTICE_SMALL_ICON);
                } else {
                    Log.e(SETTING_NOTICE, "Set notification smallIcon error, R." +
                            DEFAULT_RES_ICON_TYPE + "." + DEFAULT_NOTICE_SMALL_ICON + " not found.");
                }
                break;
            // 指定状态栏图标资源Id
            case R.id.assign_smallicon:
                /**
                 * 此处指定资源Id为R.raw.alicloud_notification_smallicon_assgin的图标资源
                 */
                int assignSmallIconId = getResources().getIdentifier(ASSIGN_NOTICE_SMALL_ICON, DEFAULT_RES_ICON_TYPE, PackageName);
                if (assignSmallIconId != 0) {
                    PushServiceFactory.getCloudPushService().setNotificationSmallIcon(assignSmallIconId);
                    Log.i(SETTING_NOTICE, "Set notification smallIcon res id to R." + DEFAULT_RES_ICON_TYPE + "." + ASSIGN_NOTICE_SMALL_ICON);
                } else {
                    Log.e(SETTING_NOTICE, "Set notification smallIcon error, R." +
                            DEFAULT_RES_ICON_TYPE + "." + ASSIGN_NOTICE_SMALL_ICON + " not found.");
                }
                break;
        }
    }

    /**
     * 用于其他Activty跳转到该Activity
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SettingNoticeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

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
}
