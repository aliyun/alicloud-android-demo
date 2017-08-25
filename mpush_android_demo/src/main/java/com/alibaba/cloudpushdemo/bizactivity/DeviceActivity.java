package com.alibaba.cloudpushdemo.bizactivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.alibaba.cloudpushdemo.R;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;

/**
 * 查看DeviceID页面
 */
public class DeviceActivity extends Activity {

    /**
     * 用于其他Activty跳转到该Activity
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, DeviceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_device_id);

        // ActionBar 回退导航
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView deviceid = (TextView)this.findViewById(R.id.deviceid_getdeviceid);

        if (null == PushServiceFactory.getCloudPushService()) {
            deviceid.setText(getResources().getString(R.string.env_init_fail));
        } else {
            deviceid.setText(getResources().getString(R.string.start_before_get) + PushServiceFactory.getCloudPushService().getDeviceId());
            Log.i("DeviceId:",deviceid.getText().toString());
        }
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
