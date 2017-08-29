package com.alibaba.openim.feedbackdemo;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.IUnreadCountCallback;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DemoLoginActivity extends Activity implements OnClickListener {
    private final static String TAG = "DemoLoginActivity";
    private Button loginButton;
    private LinearLayout unreadCount;
    private LinearLayout scan;
    private TextView appkeyTextView;
    private int clickCount = 0;
    private boolean isGetting = false;

    private static final int CAMERA_PERMISSIONS = 1;
    private static final int STORAGE_AND_CAMERA_PERMISSIONS = 2;

    private Handler handler = new Handler(Looper.getMainLooper());

    private boolean isOpen = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_login);

        appkeyTextView = (TextView) findViewById(R.id.appkey);
        appkeyTextView.setText("AppKey:" + DemoApplication.DEFAULT_APPKEY);
        //自定义参数演示
        /*JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("loginTime", "登录时间");
            jsonObject.put("visitPath", "登陆，关于，反馈");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FeedbackAPI.setAppExtInfo(jsonObject);*/

        loginButton = (Button) findViewById(R.id.login);
        unreadCount = (LinearLayout) findViewById(R.id.unreadCountGroup);
        scan = (LinearLayout) findViewById(R.id.scanGroup);

        loginButton.setOnClickListener(this);
        scan.setOnClickListener(this);
        unreadCount.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data); //49374
        if (result != null) {
            if (result.getContents() == null) {
                isGetting = false;
                Log.e(TAG, "scan getContents is null");
            } else {
                String url = result.getContents();
                Log.d(TAG, "scan getContents: " + url);

//                String[] temp = url.split("appkey=");
//                String[] contents = temp[1].split("#");
//                String key = contents[0];
//
//                FeedbackAPI.mFeedbackCustomInfoMap.put("appkey", key);
                // note:
                // 此处由于预览提供的是http的url，但是线上必须是https的，所以这里需要hack一下
                // 为什么不是服务端改呢，这是为什么呢。。。
                // 我只是个android开发, 谢谢
                String pattern = "^(https?)";
                Pattern regex = Pattern.compile(pattern);
                Matcher m = regex.matcher(url);
                if (m.find()) {
                    String protocol = m.group(1);
                    FeedbackAPI.webviewUrl = url.replace(protocol, "https");
                } else {
                    FeedbackAPI.webviewUrl = url;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FeedbackAPI.openFeedbackActivity();
                        isGetting = false;
                    }
                }, 500);
            }
        } else {
            isGetting = false;
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        //防止重复点击, 打开多次问题
        if (isGetting) {
            return;
        }
        isGetting = true;
        switch (view.getId()) {
            case R.id.login:
                isOpen = true;
                checkForOpenOrGet(true);
                break;
            case R.id.unreadCountGroup:
                isOpen = false;
                checkForOpenOrGet(false);
                break;
            case R.id.scanGroup:
                checkForScan();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scan();
            } else {
                isGetting = false;
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_AND_CAMERA_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openOrGet(isOpen);
            } else {
                isGetting = false;
                Toast.makeText(this, "Storge or Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void checkForScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSIONS);
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

    private void checkForOpenOrGet(boolean isOpenFeedback) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}
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
        FeedbackAPI.init(this.getApplication(), DemoApplication.DEFAULT_APPKEY,DemoApplication.DEFAULT_APPSECRET);
        final Activity context = this;
        //如果500ms内init未完成, openFeedbackActivity会失败, 可以延长时间>500ms
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isOpenFeedback) {
                    FeedbackAPI.openFeedbackActivity();
                } else {
                    FeedbackAPI.getFeedbackUnreadCount(new IUnreadCountCallback() {
                        @Override
                        public void onSuccess(final int unreadCount) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(DemoLoginActivity.this, "未读数：" + unreadCount, Toast.LENGTH_SHORT);
                                    toast.show();
                                    isGetting = false;
                                }
                            });
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }
                isGetting = false;
            }
        }, 500);
    }

    //连续5次点击首页"用户反馈"标题, fragment方式进入反馈页面
    public void openFeedbackFragment(View view) {
        clickCount++;
        if (clickCount == 5) {
            clickCount = 0;
            startActivity(new Intent(this, DemoFragmentActivity.class));
        }
    }
}
