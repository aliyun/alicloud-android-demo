package com.alibaba.push.android.demo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.view.WindowCompat
import com.alibaba.push.android.demo.databinding.AddTagBinding
import com.alibaba.sdk.android.push.CloudPushService
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory

class AddTagActivity: Activity() {

    private lateinit var binding: AddTagBinding

    private var target = CloudPushService.DEVICE_TARGET
    private var alias: String? = null
    private var deviceTargetEnable = false
    private var aliasTargetEnable = false
    private var accountTargetEnable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        //设置状态栏透明
        window.statusBarColor = Color.TRANSPARENT

        binding = AddTagBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivBack.setOnClickListener { finish() }
        binding.tvCancel.setOnClickListener { finish() }
        when(intent.getIntExtra(KEY_TAG_TARGET_TYPE, -1)) {
            CloudPushService.DEVICE_TARGET -> {
                deviceTargetEnable = true
                binding.ivDeviceTagStatus.setImageResource(R.drawable.push_check)
                aliasTargetEnable = false
                binding.ivAliasTagStatus.setImageResource(R.drawable.push_unable_check)
                accountTargetEnable = false
                binding.ivAccountTagStatus.setImageResource(R.drawable.push_unable_check)
                target = CloudPushService.DEVICE_TARGET
            }
            CloudPushService.ALIAS_TARGET -> {
                deviceTargetEnable = false
                binding.ivDeviceTagStatus.setImageResource(R.drawable.push_unable_check)
                aliasTargetEnable = true
                binding.ivAliasTagStatus.setImageResource(R.drawable.push_check)
                accountTargetEnable = false
                binding.ivAccountTagStatus.setImageResource(R.drawable.push_unable_check)
                target = CloudPushService.ALIAS_TARGET
            }
            CloudPushService.ACCOUNT_TARGET -> {
                deviceTargetEnable = false
                binding.ivDeviceTagStatus.setImageResource(R.drawable.push_unable_check)
                aliasTargetEnable = false
                binding.ivAliasTagStatus.setImageResource(R.drawable.push_unable_check)
                accountTargetEnable = false
                binding.ivAccountTagStatus.setImageResource(R.drawable.push_check)
                target = CloudPushService.ACCOUNT_TARGET
            }
            else -> {
                deviceTargetEnable = true
                binding.ivDeviceTagStatus.setImageResource(R.drawable.push_check)
                getAlias()
                getAccount()
            }
        }

        binding.llDeviceTag.setOnClickListener {
            if (!deviceTargetEnable || target == CloudPushService.DEVICE_TARGET) return@setOnClickListener
            target = CloudPushService.DEVICE_TARGET
            resetCheckStatus()
            binding.ivDeviceTagStatus.setImageResource(R.drawable.push_check)
        }

        binding.llAliasTag.setOnClickListener {
            if (!aliasTargetEnable || target == CloudPushService.ALIAS_TARGET) return@setOnClickListener
            val intent = Intent(this, AliasListActivity::class.java)
            startActivityForResult(intent, 100)
        }
        binding.llAccountTag.setOnClickListener {
            if (!accountTargetEnable || target == CloudPushService.ACCOUNT_TARGET) return@setOnClickListener
            target = CloudPushService.ACCOUNT_TARGET
            resetCheckStatus()
            binding.ivAccountTagStatus.setImageResource(R.drawable.push_check)
        }

        binding.tvConfirm.setOnClickListener {
            clickConfirm()
        }
    }

    private fun clickConfirm(){
        val tag = binding.etTag.text.toString().trim()
        if (TextUtils.isEmpty(tag)) {
            toast(R.string.push_tag_input_not)
            return
        }
        setResult(RESULT_OK, Intent().apply {
            putExtra(KEY_TAG, tag)
            putExtra(KEY_TAG_TARGET_TYPE, target)
            if (target == CloudPushService.ALIAS_TARGET) {
                putExtra(KEY_ALIAS, alias)
            }
        })
        finish()
    }

    private fun resetCheckStatus(){
        if (deviceTargetEnable){
            binding.ivDeviceTagStatus.setImageResource(R.drawable.push_uncheck)
        }
        if (aliasTargetEnable){
            binding.ivAliasTagStatus.setImageResource(R.drawable.push_uncheck)
        }
        if (accountTargetEnable){
            binding.ivAccountTagStatus.setImageResource(R.drawable.push_uncheck)
        }
    }

    private fun getAlias(){
        PushServiceFactory.getCloudPushService().listAliases(object : CommonCallback {
            override fun onSuccess(response: String?) {
                aliasTargetEnable = !TextUtils.isEmpty(response)
                if (aliasTargetEnable) {
                    binding.ivAliasTagStatus.setImageResource(R.drawable.push_uncheck)
                }else {
                    binding.ivAliasTagStatus.setImageResource(R.drawable.push_unable_check)
                }

            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                aliasTargetEnable = false
                binding.ivAliasTagStatus.setImageResource(R.drawable.push_unable_check)
            }

        })
    }

    private fun getAccount(){
        val account = getSharedPreferences(
            SP_FILE_NAME,
            Context.MODE_PRIVATE
        ).getString(SP_KEY_BIND_ACCOUNT, "")
        accountTargetEnable = !TextUtils.isEmpty(account)
        if (accountTargetEnable) {
            binding.ivAccountTagStatus.setImageResource(R.drawable.push_uncheck)
            binding.tvAccount.text = account
        }else {
            binding.ivAccountTagStatus.setImageResource(R.drawable.push_unable_check)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            data?.getStringExtra(KEY_ALIAS)?.let {
                alias = it
                binding.tvAlias.text = it
                target = CloudPushService.ALIAS_TARGET
                resetCheckStatus()
                binding.ivAliasTagStatus.setImageResource(R.drawable.push_check)
            }

        }
    }

}


