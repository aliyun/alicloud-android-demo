package com.alibaba.sdk.android.ots.demo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取AndroidManifest中配置的参数信息
        Bundle metaData;
        Context context = this.getApplicationContext();
        try {
            metaData = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }
        final String accessKeyId = metaData.getString("com.alibaba.app.accesskeyid");
        final String accessKeySecret = metaData.getString("com.alibaba.app.accesskeysecret");
        final String instanceName = metaData.getString("com.alibaba.app.instancename");
        final String endPoint = metaData.getString("com.alibaba.app.endpoint");

        // 在新的线程(非UI线程)中进行OTS操作示例
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 单表操作示例
                new OTSTableOperationDemo().run(endPoint, accessKeyId, accessKeySecret, instanceName);
                // 多表操作示例
                new OTSMultiTableOperationDemo().run(endPoint, accessKeyId, accessKeySecret, instanceName);

                // 单条数据操作示例
                new OTSSingleDataDemo().run(endPoint, accessKeyId, accessKeySecret, instanceName);
                // 多条数据操作示例
                new OTSMultiDataDemo().run(endPoint, accessKeyId, accessKeySecret, instanceName);

                // 异步操作示例
                new OTSAsyncDemo().run(endPoint, accessKeyId, accessKeySecret, instanceName);
                // 自定义签名示例
                new OTSCustomSignerDemo().run(endPoint, accessKeyId, accessKeySecret, instanceName);
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
