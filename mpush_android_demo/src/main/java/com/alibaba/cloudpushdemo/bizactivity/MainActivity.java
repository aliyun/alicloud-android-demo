package com.alibaba.cloudpushdemo.bizactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.cloudpushdemo.R;
import com.alibaba.cloudpushdemo.application.MainApplication;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;

/**
 * 主页面
 */
public class MainActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "MainActivity";

    private Button btnBindAccount, btnUnbindAccount, btnTurnOnPush, btnTurnOffPush, btnBindPhoneNumber, btnUnbindPhoneNumber,
            btnBindTag, btnUnbindTag, btnListTag, btnAddAlias, btnRemoveAlias, btnListAlias;
    private TextView tvConsoleText;

    private CloudPushService mPushService;

    private void initViews() {
        this.btnBindAccount = (Button)findViewById(R.id.btnBindAccount);
        this.btnUnbindAccount = (Button)findViewById(R.id.btnUnbindAccount);
        this.btnBindPhoneNumber = (Button)findViewById(R.id.btnBindPhoneNumber);
        this.btnUnbindPhoneNumber = (Button)findViewById(R.id.btnUnbindPhoneNumber);
        this.btnTurnOnPush = (Button)findViewById(R.id.btnTurnOnPush);
        this.btnTurnOffPush = (Button)findViewById(R.id.btnTurnOffPush);
        this.btnBindTag = (Button)findViewById(R.id.btnBindTag);
        this.btnUnbindTag = (Button)findViewById(R.id.btnUnbindTag);
        this.btnListTag = (Button)findViewById(R.id.btnListTag);
        this.btnAddAlias = (Button)findViewById(R.id.btnAddAlias);
        this.btnRemoveAlias = (Button)findViewById(R.id.btnRemoveAlias);
        this.btnListAlias = (Button)findViewById(R.id.btnListAlias);
        this.tvConsoleText = (TextView)findViewById(R.id.tvConsoleText);

        this.btnBindAccount.setOnClickListener(this);
        this.btnUnbindAccount.setOnClickListener(this);
        this.btnBindPhoneNumber.setOnClickListener(this);
        this.btnUnbindPhoneNumber.setOnClickListener(this);
        this.btnTurnOnPush.setOnClickListener(this);
        this.btnTurnOffPush.setOnClickListener(this);
        this.btnBindTag.setOnClickListener(this);
        this.btnUnbindTag.setOnClickListener(this);
        this.btnListTag.setOnClickListener(this);
        this.btnAddAlias.setOnClickListener(this);
        this.btnRemoveAlias.setOnClickListener(this);
        this.btnListAlias.setOnClickListener(this);
    }

    /**
     * 绑定账户接口:CloudPushService.bindAccount调用示例
     * 1. 绑定账号后,可以在服务端通过账号进行推送
     * 2. 一个设备只能绑定一个账号
     */
    private void bindAccount() {
        final EditText etAccount = new EditText(MainActivity.this);
        new AlertDialog.Builder(MainActivity.this).setTitle(R.string.settings_bindaccount).setView(etAccount)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (etAccount.getEditableText() != null) {
                            final String account = etAccount.getEditableText().toString();
                            if (account.length() > 0) {
                                mPushService.bindAccount(account, new CommonCallback() {
                                    @Override
                                    public void onSuccess(String s) {
                                        tvConsoleText.append("bind account " + account + " success\n");
                                    }

                                    @Override
                                    public void onFailed(String errorCode, String errorMsg) {
                                        tvConsoleText.append("bind account " + account + " failed." +
                                                                "errorCode: " + errorCode + ", errorMsg:" + errorMsg);
                                    }
                                });
                            }
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
    }

    /**
     * 解绑账户接口:CloudPushService.unbindAccount调用示例
     * 1. 调用该接口后,设备与账号的绑定关系解除
     */
    private void unBindAccount() {
        mPushService.unbindAccount(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                tvConsoleText.append("unbind account success\n");
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                tvConsoleText.append("bind account failed." +
                        "errorCode: " + errorCode + ", errorMsg:" + errorMsg + "\n");
            }
        });
    }

    /**
     * 绑定电话接口:CloudPushService.bindPhoneNumber调用示例
     * 1. 绑定手机功能主要是方便用户接入复合推送功能:当推送消息无法到达时,通过手机短信的方式提示用户
     * 2. 复合推送接入详情请参考:https://help.aliyun.com/document_detail/57008.html
     */
    private void bindPhoneNumber() {
        final EditText etAccount = new EditText(MainActivity.this);
        new AlertDialog.Builder(MainActivity.this).setTitle(R.string.setting_bindPhoneNumber).setView(etAccount)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (etAccount.getEditableText() != null) {
                            final String phoneNumber = etAccount.getEditableText().toString();
                            if (phoneNumber.length() > 0) {
                                mPushService.bindPhoneNumber(phoneNumber, new CommonCallback() {
                                    @Override
                                    public void onSuccess(String s) {
                                        tvConsoleText.append("bind phone number " + phoneNumber + " success\n");
                                    }

                                    @Override
                                    public void onFailed(String errorCode, String errorMsg) {
                                        tvConsoleText.append("bind phone number " + phoneNumber + " failed." +
                                                "errorCode: " + errorCode + ", errorMsg:" + errorMsg + "\n");
                                    }
                                });
                            }
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
    }

    /**
     * 解绑电话接口:CloudPushService.unbindPushService调用示例
     */
    private void unbindPhoneNumber() {
        mPushService.unbindPhoneNumber(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                tvConsoleText.append("unbind phone number success\n");
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                tvConsoleText.append("bind phone number " + " failed." +
                        "errorCode: " + errorCode + ", errorMsg:" + errorMsg + "\n");
            }
        });
    }

    /**
     * 打开推送通道接口:CloudPushService.turnOnPushChannel调用示例
     * 1. 推送通道默认是打开的,如果没有调用turnOffPushChannel接口关闭推送通道,无法调用该接口
     */
    private void turnOnPush() {
        mPushService.turnOnPushChannel(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                tvConsoleText.append("turn on push channel success\n");
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                tvConsoleText.append("turn on push channel failed." +
                        "errorCode: " + errorCode + ", errorMsg:" + errorMsg + "\n");
            }
        });
    }

    /**关闭推送通道接口:CloudPushService.turnOffPushChannel调用示例
     * 1. 调用该接口后,移动推送服务端将不再向该设备推送消息
     * 2. 关闭推送通道,必须通过调用turnOnPushChannel才能让服务端重新向该设备推送消息
     */
    private void turnOffPush() {
        mPushService.turnOffPushChannel(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                tvConsoleText.append("turn off push channel success\n");
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                tvConsoleText.append("turn off push channel failed." +
                        "errorCode: " + errorCode + ", errorMsg:" + errorMsg + "\n");
            }
        });
    }

    /**
     * 绑定标签接口:CloudPushService.bindTag调用示例
     * 1. 标签可以绑定到设备、账号和别名上,此处demo展示的是绑定标签到设备
     * 2. 绑定标签完成后,即可通过标签推送消息
     */
    private void bindTag() {
        final EditText etTag = new EditText(MainActivity.this);
        new AlertDialog.Builder(MainActivity.this).setTitle(R.string.settings_bindTagToDev).setView(etTag)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (etTag.getEditableText() != null) {
                            final String tag = etTag.getEditableText().toString();
                            if (tag.length() > 0) {
                                mPushService.bindTag(CloudPushService.DEVICE_TARGET, new String[]{ tag }, null, new CommonCallback() {
                                    @Override
                                    public void onSuccess(String s) {
                                        tvConsoleText.append("bind tag " + tag + " success\n");
                                    }

                                    @Override
                                    public void onFailed(String errorCode, String errorMsg) {
                                        tvConsoleText.append("bind tag " + tag + " failed." +
                                                "errorCode: " + errorCode + ", errorMsg:" + errorMsg + "\n");
                                    }
                                });
                            }
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
    }

    /**
     * 解绑标签接口:CloudPushService.unbindTag调用示例
     * 1. 标签可以从设备、账号和别名解绑,此处demo展示的是解绑绑定到设备的标签
     */
    private void unbindTag() {
        final EditText etTag = new EditText(MainActivity.this);
        new AlertDialog.Builder(MainActivity.this).setTitle(R.string.settings_unbindTagFromDev).setView(etTag)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (etTag.getEditableText() != null) {
                            final String tag = etTag.getEditableText().toString();
                            if (tag.length() > 0) {
                                mPushService.unbindTag(CloudPushService.DEVICE_TARGET, new String[]{ tag }, null, new CommonCallback() {
                                    @Override
                                    public void onSuccess(String s) {
                                        tvConsoleText.append("unbind tag " + tag + " success\n");
                                    }

                                    @Override
                                    public void onFailed(String errorCode, String errorMsg) {
                                        tvConsoleText.append("unbind tag " + tag + " failed." +
                                                "errorCode: " + errorCode + ", errorMsg:" + errorMsg + "\n");
                                    }
                                });
                            }
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
    }

    /**
     * 列出tag接口:CloudPushService.listTags调用示例
     * 1. listTags当前仅支持列出该设备所绑定的标签
     */
    private void listTags() {
        mPushService.listTags(CloudPushService.DEVICE_TARGET, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                tvConsoleText.append("tags:" + response + " \n");
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                tvConsoleText.append("list tags failed. errorCode:" + errorCode + " errorMsg:" + errorMsg);
            }
        });
    }

    /**
     * 添加别名接口:CloudPushService.addAlias调用示例
     * 1. 调用接口后,可以通过别名进行推送
     */
    private void addAlias() {
        final EditText etAlias = new EditText(MainActivity.this);
        new AlertDialog.Builder(MainActivity.this).setTitle(R.string.settings_addAlias).setView(etAlias)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (etAlias.getEditableText() != null) {
                            final String alias = etAlias.getEditableText().toString();
                            if (alias.length() > 0) {
                                mPushService.addAlias(alias, new CommonCallback() {
                                    @Override
                                    public void onSuccess(String s) {
                                        tvConsoleText.append("add alias " + alias + " success\n");
                                    }

                                    @Override
                                    public void onFailed(String errorCode, String errorMsg) {
                                        tvConsoleText.append("add alias " + alias + " failed." +
                                                "errorCode: " + errorCode + ", errorMsg:" + errorMsg + "\n");
                                    }
                                });
                            }
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
    }

    /**
     * 删除别名接口:CloudPushService.removeAlias调用示例
     */
    private void removeAlias() {
        final EditText etAlias = new EditText(MainActivity.this);
        new AlertDialog.Builder(MainActivity.this).setTitle(R.string.settings_removeAlias).setView(etAlias)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (etAlias.getEditableText() != null) {
                            final String alias = etAlias.getEditableText().toString();
                            if (alias.length() > 0) {
                                mPushService.removeAlias(alias, new CommonCallback() {
                                    @Override
                                    public void onSuccess(String s) {
                                        tvConsoleText.append("remove alias " + alias + " success\n");
                                    }

                                    @Override
                                    public void onFailed(String errorCode, String errorMsg) {
                                        tvConsoleText.append("remove alias " + alias + " failed." +
                                                "errorCode: " + errorCode + ", errorMsg:" + errorMsg + "\n");
                                    }
                                });
                            }
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
    }

    /**
     * 列出别名接口:CloudPushService.listAlias调用示例
     */
    private void listAlias() {
        mPushService.listAliases(new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                tvConsoleText.append("aliases:" + response + " \n");
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                tvConsoleText.append("list aliases failed. errorCode:" + errorCode + " errorMsg:" + errorMsg);
            }
        });
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainApplication.setMainActivity(this);
        this.setContentView(R.layout.demo_activity_main);
        this.initViews();
        mPushService = PushServiceFactory.getCloudPushService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lzlmain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about_us:  // 点击 关于我们 选项卡
                AboutActivity.actionStart(this.getApplicationContext());
                return true;
            case R.id.action_clear_tips:  // 点击 清空消息 选项卡
                this.tvConsoleText.setText("");
                return true;
            case R.id.action_deviceid:  // 点击 查看DeviceID 选项卡
                DeviceActivity.actionStart(this.getApplicationContext());
                return true;
            case R.id.action_helper:  // 点击 帮助中心 选项卡
                HelperActivity.actionStart(this.getApplicationContext());
                return true;
            case R.id.action_settings_notice:   // 点击 通知设置
                SettingNoticeActivity.actionStart(this.getApplicationContext());
                return true;
            case R.id.action_third_push_activity:
                ThirdPushPopupActivity.actionStart(this.getApplicationContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBindAccount:
                this.bindAccount();
                break;
            case R.id.btnUnbindAccount:
                this.unBindAccount();
                break;
            case R.id.btnBindPhoneNumber:
                this.bindPhoneNumber();
                break;
            case R.id.btnUnbindPhoneNumber:
                this.unbindPhoneNumber();
                break;
            case R.id.btnTurnOnPush:
                this.turnOnPush();
                break;
            case R.id.btnTurnOffPush:
                this.turnOffPush();
                break;
            case R.id.btnBindTag:
                this.bindTag();
                break;
            case R.id.btnUnbindTag:
                this.unbindTag();
                break;
            case R.id.btnListTag:
                this.listTags();
                break;
            case R.id.btnAddAlias:
                this.addAlias();
                break;
            case R.id.btnRemoveAlias:
                this.removeAlias();
                break;
            case R.id.btnListAlias:
                this.listAlias();
                break;
        }
    }

    public void appendConsoleText(String text) {
        this.tvConsoleText.append(text + "\n");
    }

}
