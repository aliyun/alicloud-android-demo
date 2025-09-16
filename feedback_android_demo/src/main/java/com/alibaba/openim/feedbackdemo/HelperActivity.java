package com.alibaba.openim.feedbackdemo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 帮助中心页面
 */
public class HelperActivity extends Activity {
    private static final String TAG = "HelperActivity";
    /**
     * 用于其他Activty跳转到该Activity
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, HelperActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_helper_page);

        // ActionBar 回退导航
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Log.i(TAG, this.getResources().getString(R.string.layout_helpus_content));
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
