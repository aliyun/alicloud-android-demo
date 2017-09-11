package com.alibaba.openim.feedbackdemo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.IUnreadCountCallback;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class DemoLoginActivity extends Activity implements OnClickListener {
    private final static String TAG = "DemoLoginActivity";
    private View btnOpenActivity,btnOpenFragment,btnGetUnreadCount,btnScan;
    private TextView tvConsoleText;

    private static final int CAMERA_PERMISSIONS = 1;
    private static final int STORAGE_AND_CAMERA_PERMISSIONS = 2;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_login);

        tvConsoleText = (TextView)findViewById(R.id.tvConsoleText);
        btnOpenActivity = findViewById(R.id.btnOpenActivity);
        btnGetUnreadCount = findViewById(R.id.btnUnread);
        btnOpenFragment = findViewById(R.id.btnOpenFragment);
        btnScan = findViewById(R.id.btnScan);

        btnOpenActivity.setOnClickListener(this);
        btnGetUnreadCount.setOnClickListener(this);
        btnOpenFragment.setOnClickListener(this);
        btnScan.setOnClickListener(this);

        tvConsoleText.append("AppKey:" + DemoApplication.DEFAULT_APPKEY+"\n");
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOpenActivity:
                tvConsoleText.append("open feedback activity\n");
                checkForOpenOrGet(true);
                break;
            case R.id.btnUnread:
                getFeedbackUnreadCount();
                break;
            case R.id.btnScan:
                tvConsoleText.append("open scan\n");
                checkForScan();
                break;
            case R.id.btnOpenFragment:
                tvConsoleText.append("open feedback fragment\n");
                startActivity(new Intent(this, DemoFragmentActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.e(TAG, "scan getContents is null");
            } else {
                String url = result.getContents();
                Log.d(TAG, "scan getContents: " + url);
                String pattern = "^(https?)";
                Pattern regex = Pattern.compile(pattern);
                Matcher m = regex.matcher(url);
                if (m.find()) {
                    String protocol = m.group(1);
                    FeedbackAPI.setWebViewUrl(url.replace(protocol, "https"));
                } else {
                    FeedbackAPI.setWebViewUrl(url);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FeedbackAPI.openFeedbackActivity();
                    }
                }, 500);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scan();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                tvConsoleText.append("Camera Permission Denied\n");
            }
        } else if (requestCode == STORAGE_AND_CAMERA_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openOrGet(true);
            } else {
                Toast.makeText(this, "Storge or Camera Permission Denied", Toast.LENGTH_SHORT).show();
                tvConsoleText.append("Storge or Camera Permission Denied\n");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 扫码预览
     */
    public void checkForScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSIONS);
        } else {
            scan();
        }
    }

    public void scan() {
        IntentIntegrator integrator = new IntentIntegrator(DemoLoginActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    /**
     * 打开feedback的activity
     * 首先检查需要的权限
     * @param isOpenFeedback
     */
    private void checkForOpenOrGet(boolean isOpenFeedback) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO}
                , STORAGE_AND_CAMERA_PERMISSIONS);
        } else {
            openOrGet(isOpenFeedback);
        }
    }

    /**
     * @param isOpenFeedback 打开网页or获取未读数
     */
    private void openOrGet(final boolean isOpenFeedback) {
        //接入方不需要这样调用, 因为扫码预览, 同时为了服务器发布后能做到实时预览效果, 所有每次都init.
        //业务方默认只需要init一次, 然后直接openFeedbackActivity/getFeedbackUnreadCount即可
        FeedbackAPI.init(this.getApplication(), DemoApplication.DEFAULT_APPKEY, DemoApplication.DEFAULT_APPSECRET);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isOpenFeedback) {
                    FeedbackAPI.openFeedbackActivity();
                }
            }
        }, 500);
    }

    /**
     * 获取未读消息数
     */
    private void getFeedbackUnreadCount(){
        tvConsoleText.append("get unread count\n");
        FeedbackAPI.getFeedbackUnreadCount(new IUnreadCountCallback() {
            @Override
            public void onSuccess(final int unreadCount) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(DemoLoginActivity.this, "未读数：" + unreadCount,
                            Toast.LENGTH_SHORT);
                        toast.show();
                        tvConsoleText.append("get unread count success. unreadCount = "+unreadCount+"\n");
                    }
                });
            }

            @Override
            public void onError(final int i,final String s) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvConsoleText.append("get unread count failed:. code="+i+" msg="+s+"\n");
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_about_us:
                AboutActivity.actionStart(this.getApplicationContext());
                return true;
            case R.id.action_helper:
                HelperActivity.actionStart(this.getApplicationContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
