package com.alibaba.cloudpushdemo.bizactivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.cloudpushdemo.R;
import com.alibaba.cloudpushdemo.adapter.CheckerListAdapter;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;

import java.util.ArrayList;

/**
 * 故障排查页面
 */
public class CheckerActivity extends Activity {

    private ListView checkerList = null;
    ListAdapter adapter;
    ArrayList<String> checkerData = new ArrayList<String>();

    ProgressBar bar;

    /**
     * 用于其他Activty跳转到该Activity
     *
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, CheckerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // 诊断线程
    class CheckerTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            // TODO Task startup.
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //更新进度条
            bar.setProgress(values[0]);

            // Update list view.
            updateCheckerList();
        }

        @Override
        protected Integer doInBackground(String... params) {
            // Saying checker service.
            checkerData.add(getResources().getString(R.string.checker_start_service));
            publishProgress(1);

            // checking network status.
            ConnectivityManager connManager = (ConnectivityManager) CheckerActivity.this.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                checkerData.add(getResources().getString(R.string.checker_network_success));
                publishProgress(2);
            } else {
                checkerData.add(getResources().getString(R.string.checker_network_fail));
                publishProgress(2);
                checkerData.add(getResources().getString(R.string.checker_over));
                publishProgress(3);
                return null;
            }

            // check sdk service
            if (PushServiceFactory.getCloudPushService() != null) {
                checkerData.add(getResources().getString(R.string.env_init_success));
                publishProgress(3);
            }else {
                checkerData.add(getResources().getString(R.string.env_init_fail));
                publishProgress(3);
                checkerData.add(getResources().getString(R.string.checker_over));
                publishProgress(4);
                return null;
            }

            // Add other check

            checkerData.add(getResources().getString(R.string.checker_over));
            publishProgress(10);
            return null;
        }
    }

    /**
     * Update the checker list view.
     */
    private void updateCheckerList() {
        // 设置诊断清单参数
        checkerList = (ListView) findViewById(R.id.checkList);
        adapter = new CheckerListAdapter(getApplicationContext(), checkerData);
        checkerList.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checker);

        // ActionBar 回退导航
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // 设置进度条参数
        bar = (ProgressBar) findViewById(R.id.checkProgress);
        bar.setMax(10);

        this.updateCheckerList();

        // Task Start.
        new CheckerTask().execute();
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