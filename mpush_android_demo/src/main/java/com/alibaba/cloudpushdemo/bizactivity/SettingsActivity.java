package com.alibaba.cloudpushdemo.bizactivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.cloudpushdemo.R;
import com.alibaba.sdk.android.AlibabaSDK;

/**
 * 设置中心页面
 */
public class SettingsActivity extends Activity implements View.OnClickListener {

    public static final String SETTINGS_ACT = "settings-act";

    String useraccount;
    String userlabel;

    Button bindAccount;
    Button unbindAccount;
    Button bindLabel;
    Button unbindLabel;

    /**
     * 用于其他Activty跳转到该Activity
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Log.d(SETTINGS_ACT, "@切换到 Activity ：SettinsActivity");
    }

    /**
     * 使用一个监听方法监听所有按钮
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bindAccount:
                useraccount = ((EditText)this.findViewById(R.id.userAccount)).getText().toString().trim();
                if (!useraccount.equals("") && null != useraccount) {
                    AlibabaSDK.getService(CloudPushService.class).bindAccount(useraccount, new CommonCallback() {
                        @Override
                        public void onSuccess() {

                            // 清空文本框
                            ((EditText)SettingsActivity.this.findViewById(R.id.userAccount)).setText("");

                            // 本地存储绑定的用户名
                            SharedPreferences accountStore = SettingsActivity.this.getSharedPreferences("account", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = accountStore.edit();
                            editor.putString("accountName", useraccount);
                            editor.commit();

                            Toast.makeText(getApplicationContext(), "赞! 账号绑定成功 ~",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(SETTINGS_ACT, "@用户绑定账号 ：" + useraccount + " 成功");
                        }

                        @Override
                        public void onFailed(String errorCode, String errorMessage) {
                            Toast.makeText(getApplicationContext(), "衰! 账号绑定失败 ~",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(SETTINGS_ACT, "@用户绑定账号 ：" + useraccount + " 失败，原因 ： " + errorMessage);
                        }
                    });

                    // 关闭软键盘
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(this.findViewById(R.id.userAccount).getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS) ;
                } else {
                    Toast.makeText(getApplicationContext(), "请输入用户名，谢谢",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.unbindAccount:
                AlibabaSDK.getService(CloudPushService.class).unbindAccount(
                        new CommonCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(), "赞! 账号解绑成功 ~",
                                        Toast.LENGTH_SHORT).show();
                                Log.d(SETTINGS_ACT, "@用户解绑账户 ：" + useraccount + " 成功");
                                // 删除本地存储的用户名
                                SharedPreferences accountStore = SettingsActivity.this.getSharedPreferences("account", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = accountStore.edit();
                                editor.remove("accountName");
                                editor.commit();
                            }

                            @Override
                            public void onFailed(String errorCode, String errorMessage) {
                                Toast.makeText(getApplicationContext(), "衰! 账号解绑失败 ~",
                                        Toast.LENGTH_SHORT).show();
                                Log.d(SETTINGS_ACT, "@用户解绑账户 ：" + useraccount + " 失败，原因 ：" + errorMessage);
                            }
                        }
                );
                break;
            case R.id.bindLabel:
                userlabel = ((EditText)this.findViewById(R.id.userLabel)).getText().toString().trim();
                if (!userlabel.equals("") && null != userlabel) {
                    AlibabaSDK.getService(CloudPushService.class).addTag(userlabel, new CommonCallback() {
                        @Override
                        public void onSuccess() {
                            // 清空文本框
                            ((EditText)SettingsActivity.this.findViewById(R.id.userLabel)).setText("");

                            Toast.makeText(getApplicationContext(), "赞! 增加标签成功 ~",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(SETTINGS_ACT, "@用户增加标签 ：" + userlabel + " 成功");
                        }

                        @Override
                        public void onFailed(String errorCode, String errorMessage) {
                            Toast.makeText(getApplicationContext(), "衰! 增加标签失败 ~",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(SETTINGS_ACT, "@用户增加标签 ：" + userlabel + " 失败，原因：" + errorMessage);
                        }
                    });

                    // 关闭软键盘
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(this.findViewById(R.id.userLabel).getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS) ;
                } else {
                    Toast.makeText(getApplicationContext(), "请输入标签，谢谢~",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.unbindLabel:
                userlabel = ((EditText)this.findViewById(R.id.userLabel)).getText().toString().trim();
                if (!userlabel.equals("") && null != userlabel) {
                    AlibabaSDK.getService(CloudPushService.class).removeTag(userlabel, new CommonCallback() {
                        @Override
                        public void onSuccess() {
                            // 清空文本框
                            ((EditText)SettingsActivity.this.findViewById(R.id.userLabel)).setText("");

                            Toast.makeText(getApplicationContext(), "赞! 删除标签成功 ~",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(SETTINGS_ACT, "@用户删除标签 ：" + userlabel + " 成功");
                        }

                        @Override
                        public void onFailed(String errorCode, String errorMessage) {
                            Toast.makeText(getApplicationContext(), "衰! 删除标签失败 ~",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(SETTINGS_ACT, "@用户删除标签 ：" + userlabel + " 失败，原因：" + errorMessage);
                        }
                    });

                    // 关闭软键盘
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(this.findViewById(R.id.userLabel).getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS) ;
                } else {
                    Toast.makeText(getApplicationContext(), "请输入标签，谢谢~",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // ActionBar 回退导航
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        bindAccount = (Button)this.findViewById(R.id.bindAccount);
        unbindAccount = (Button)this.findViewById(R.id.unbindAccount);
        bindLabel = (Button)this.findViewById(R.id.bindLabel);
        unbindLabel = (Button)this.findViewById(R.id.unbindLabel);


        bindAccount.setOnClickListener(this);
        unbindAccount.setOnClickListener(this);
        bindLabel.setOnClickListener(this);
        unbindLabel.setOnClickListener(this);
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
