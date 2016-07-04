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
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.cloudpushdemo.R;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;

/**
 * 设置中心页面
 */
public class SettingsActivity extends Activity implements View.OnClickListener {

    public static final String SETTINGS_ACT = "settings-act";

    String acountStr;
    String tagStr;
    String aliasStr;

    Button bindAccount;
    Button unbindAccount;
    Button addAlias;
    Button removeAlias;
    Button bindTagToDev;
    Button unbindTagFromDev;
    Button bindTagToAccount;
    Button unbindTagFromAccount;
    Button bindTagToAlias;
    Button unbindTagFromAlias;
    Button listTags;
    Button listAliases;

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

    private String[] getTagArr() {
        tagStr = ((EditText)this.findViewById(R.id.inputTags)).getText().toString();
        String[] tagArr = null;
        if (tagStr != null && tagStr.length() > 0) {
            tagArr = tagStr.split(" ");
        } else {
            Toast.makeText(getApplicationContext(), "请按照格式输入标签.", Toast.LENGTH_SHORT).show();
        }
        return tagArr;
    }

    /**
     * 使用一个监听方法监听所有按钮
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()) {
            // 绑定账号
            case R.id.bindAccount:
                acountStr = ((EditText)this.findViewById(R.id.userAccount)).getText().toString().trim();
                if (!acountStr.equals("") && null != acountStr) {
                    PushServiceFactory.getCloudPushService().bindAccount(acountStr, new CommonCallback() {
                        @Override
                        public void onSuccess(String response) {
                            // 清空文本框
                            ((EditText)SettingsActivity.this.findViewById(R.id.userAccount)).setText("");
                            // 本地存储绑定的用户名
                            SharedPreferences accountStore = SettingsActivity.this.getSharedPreferences("account", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = accountStore.edit();
                            editor.putString("accountName", acountStr);
                            editor.commit();
                            Toast.makeText(getApplicationContext(), "赞! 账号绑定成功 ~",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(SETTINGS_ACT, "@用户绑定账号 ：" + acountStr + " 成功");
                        }

                        @Override
                        public void onFailed(String errorCode, String errorMessage) {
                            Toast.makeText(getApplicationContext(), "衰! 账号绑定失败 ~",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(SETTINGS_ACT, "@用户绑定账号 ：" + acountStr + " 失败，原因 ： " + errorMessage);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "请输入账号，谢谢",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            // 解绑账号
            case R.id.unbindAccount:
                PushServiceFactory.getCloudPushService().unbindAccount(
                        new CommonCallback() {
                            @Override
                            public void onSuccess(String response) {
                                Toast.makeText(getApplicationContext(), "赞! 账号解绑成功 ~",
                                        Toast.LENGTH_SHORT).show();
                                Log.d(SETTINGS_ACT, "@用户解绑账户 ：" + acountStr + " 成功");
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
                                Log.d(SETTINGS_ACT, "@用户解绑账户 ：" + acountStr + " 失败，原因 ：" + errorMessage);
                            }
                        }
                );
                break;
            // 绑定标签到设备
            case R.id.bindTagToDev:
                if (getTagArr() == null) return;
                PushServiceFactory.getCloudPushService().bindTag(1, getTagArr(), null, new CommonCallback() {
                    @Override
                    public void onSuccess(String response) {
                        ((EditText)SettingsActivity.this.findViewById(R.id.inputTags)).setText("");
                        Toast.makeText(getApplicationContext(), "绑定标签到设备成功.", Toast.LENGTH_SHORT).show();
                        Log.d(SETTINGS_ACT, "绑定标签到设备成功.");
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {
                        Log.e(SETTINGS_ACT, "绑定标签到设备失败，errorCode: " + errorCode + ", errorMessage：" + errorMessage);
                        Toast.makeText(getApplicationContext(), "绑定标签到设备失败，原因：" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            // 解绑设备标签
            case R.id.unbindTagFromDev:
                if (getTagArr() == null) return;
                PushServiceFactory.getCloudPushService().unbindTag(1, getTagArr(), null, new CommonCallback() {
                    @Override
                    public void onSuccess(String response) {
                        ((EditText)SettingsActivity.this.findViewById(R.id.inputTags)).setText("");
                        Toast.makeText(getApplicationContext(), "解绑设备标签成功.", Toast.LENGTH_SHORT).show();
                        Log.d(SETTINGS_ACT, "解绑设备标签成功.");
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {
                        Log.e(SETTINGS_ACT, "解绑设备标签失败，errorCode: " + errorCode + ", errorMessage：" + errorMessage);
                        Toast.makeText(getApplicationContext(), "解绑设备标签失败，原因：" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            // 绑定标签到账号
            case R.id.bindTagToAccount:
                if (getTagArr() == null) return;
                PushServiceFactory.getCloudPushService().bindTag(2, getTagArr(), null, new CommonCallback() {
                    @Override
                    public void onSuccess(String response) {
                        ((EditText)SettingsActivity.this.findViewById(R.id.inputTags)).setText("");
                        Toast.makeText(getApplicationContext(), "绑定标签到账号成功.", Toast.LENGTH_SHORT).show();
                        Log.d(SETTINGS_ACT, "绑定标签到账号成功.");
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {
                        Log.e(SETTINGS_ACT, "绑定标签到账号失败，errorCode: " + errorCode + ", errorMessage：" + errorMessage);
                        Toast.makeText(getApplicationContext(), "绑定标签到账号失败，原因：" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            // 解绑账号标签
            case R.id.unbindTagFromAccount:
                if (getTagArr() == null) return;
                PushServiceFactory.getCloudPushService().unbindTag(2, getTagArr(), null, new CommonCallback() {
                    @Override
                    public void onSuccess(String response) {
                        ((EditText)SettingsActivity.this.findViewById(R.id.inputTags)).setText("");
                        Toast.makeText(getApplicationContext(), "解绑账号标签成功.", Toast.LENGTH_SHORT).show();
                        Log.d(SETTINGS_ACT, "解绑账号标签成功.");
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {
                        Log.e(SETTINGS_ACT, "解绑账号标签失败，errorCode: " + errorCode + ", errorMessage：" + errorMessage);
                        Toast.makeText(getApplicationContext(), "解绑账号标签失败，原因：" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            // 绑定标签到别名
            case R.id.bindTagToAlias:
                if (getTagArr() == null) return;
                aliasStr = ((EditText)this.findViewById(R.id.inputAlias)).getText().toString();
                if (aliasStr == null || aliasStr.length() == 0) {
                    Toast.makeText(getApplicationContext(), "请输入别名，谢谢", Toast.LENGTH_SHORT).show();
                    return;
                }
                PushServiceFactory.getCloudPushService().bindTag(3, getTagArr(), aliasStr, new CommonCallback() {
                    @Override
                    public void onSuccess(String response) {
                        ((EditText)SettingsActivity.this.findViewById(R.id.inputTags)).setText("");
                        ((EditText)SettingsActivity.this.findViewById(R.id.inputAlias)).setText("");
                        Toast.makeText(getApplicationContext(), "绑定标签到别名成功.", Toast.LENGTH_SHORT).show();
                        Log.d(SETTINGS_ACT, "绑定标签到别名成功.");
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {
                        Log.e(SETTINGS_ACT, "绑定标签到别名失败，errorCode: " + errorCode + ", errorMessage：" + errorMessage);
                        Toast.makeText(getApplicationContext(), "绑定标签到别名失败，原因：" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            // 解绑别名标签
            case R.id.unbindTagFromAlias:
                if (getTagArr() == null) return;
                aliasStr = ((EditText)this.findViewById(R.id.inputAlias)).getText().toString();
                if (aliasStr == null || aliasStr.length() == 0) {
                    Toast.makeText(getApplicationContext(), "请输入别名，谢谢", Toast.LENGTH_SHORT).show();
                    return;
                }
                PushServiceFactory.getCloudPushService().unbindTag(3, getTagArr(), aliasStr, new CommonCallback() {
                    @Override
                    public void onSuccess(String response) {
                        ((EditText)SettingsActivity.this.findViewById(R.id.inputTags)).setText("");
                        ((EditText)SettingsActivity.this.findViewById(R.id.inputAlias)).setText("");
                        Toast.makeText(getApplicationContext(), "解绑别名标签成功.", Toast.LENGTH_SHORT).show();
                        Log.d(SETTINGS_ACT, "解绑别名标签成功.");
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {
                        Log.e(SETTINGS_ACT, "解绑别名标签失败，errorCode: " + errorCode + ", errorMessage：" + errorMessage);
                        Toast.makeText(getApplicationContext(), "解绑别名标签失败，原因：" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            // 查询设备标签
            case R.id.listTags:
                PushServiceFactory.getCloudPushService().listTags(1, new CommonCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(getApplicationContext(), "查询设备标签成功，标签：" + response, Toast.LENGTH_SHORT).show();
                        Log.d(SETTINGS_ACT, "解绑别名标签成功，标签：" + response);
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {
                        Log.e(SETTINGS_ACT, "查询设备标签失败，errorCode: " + errorCode + ", errorMessage：" + errorMessage);
                        Toast.makeText(getApplicationContext(), "查询设备标签失败，原因：" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            // 查询设备别名
            case R.id.listAliases:
                PushServiceFactory.getCloudPushService().listAliases(new CommonCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(getApplicationContext(), "查询设备别名成功，别名：" + response, Toast.LENGTH_SHORT).show();
                        Log.d(SETTINGS_ACT, "解绑别名标签成功，标签：" + response);
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {
                        Log.e(SETTINGS_ACT, "查询设备别名失败，errorCode: " + errorCode + ", errorMessage：" + errorMessage);
                        Toast.makeText(getApplicationContext(), "查询设备别名失败，原因：" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            // 添加设备别名
            case R.id.addAlias:
                aliasStr = ((EditText)this.findViewById(R.id.inputAlias)).getText().toString();
                if (aliasStr != null && aliasStr.length() > 0) {
                    PushServiceFactory.getCloudPushService().addAlias(aliasStr, new CommonCallback() {
                        @Override
                        public void onSuccess(String response) {
                            ((EditText)SettingsActivity.this.findViewById(R.id.inputAlias)).setText("");
                            Toast.makeText(getApplicationContext(), "添加别名成功.", Toast.LENGTH_SHORT).show();
                            Log.d(SETTINGS_ACT, "添加别名成功.");
                        }

                        @Override
                        public void onFailed(String errorCode, String errorMessage) {
                            Log.e(SETTINGS_ACT, "添加别名失败，errorCode: " + errorCode + ", errorMessage：" + errorMessage);
                            Toast.makeText(getApplicationContext(), "添加别名失败，原因：" + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "请输入别名，谢谢", Toast.LENGTH_SHORT).show();
                }
                break;
            // 删除设备别名
            case R.id.removeAlias:
                aliasStr = ((EditText)this.findViewById(R.id.inputAlias)).getText().toString();
                if (aliasStr == null || aliasStr.length() == 0) {
                    Log.d(SETTINGS_ACT, "删除设备全部别名");
                }
                PushServiceFactory.getCloudPushService().removeAlias(aliasStr, new CommonCallback() {
                    @Override
                    public void onSuccess(String response) {
                        ((EditText)SettingsActivity.this.findViewById(R.id.inputAlias)).setText("");
                        Toast.makeText(getApplicationContext(), "删除别名成功.", Toast.LENGTH_SHORT).show();
                        Log.d(SETTINGS_ACT, "删除别名成功.");
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {
                        Log.e(SETTINGS_ACT, "删除别名失败，errorCode: " + errorCode + ", errorMessage：" + errorMessage);
                        Toast.makeText(getApplicationContext(), "删除别名失败，原因：" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
                break;
        }
        // 关闭软键盘
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.findViewById(R.id.userAccount).getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
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
        addAlias = (Button)this.findViewById(R.id.addAlias);
        removeAlias = (Button)this.findViewById(R.id.removeAlias);
        bindTagToDev = (Button)this.findViewById(R.id.bindTagToDev);
        unbindTagFromDev = (Button)this.findViewById(R.id.unbindTagFromDev);
        bindTagToAccount = (Button)this.findViewById(R.id.bindTagToAccount);
        unbindTagFromAccount = (Button)this.findViewById(R.id.unbindTagFromAccount);
        bindTagToAlias = (Button)this.findViewById(R.id.bindTagToAlias);
        unbindTagFromAlias = (Button)this.findViewById(R.id.unbindTagFromAlias);
        listTags = (Button)this.findViewById(R.id.listTags);
        listAliases = (Button)this.findViewById(R.id.listAliases);

        bindAccount.setOnClickListener(this);
        unbindAccount.setOnClickListener(this);
        addAlias.setOnClickListener(this);
        removeAlias.setOnClickListener(this);
        bindTagToDev.setOnClickListener(this);
        unbindTagFromDev.setOnClickListener(this);
        bindTagToAccount.setOnClickListener(this);
        unbindTagFromAccount.setOnClickListener(this);
        bindTagToAlias.setOnClickListener(this);
        unbindTagFromAlias.setOnClickListener(this);
        listTags.setOnClickListener(this);
        listAliases.setOnClickListener(this);
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
