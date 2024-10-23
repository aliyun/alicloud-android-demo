package com.alibaba.push.android.demo

import android.app.Activity
import android.content.Context
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

    private var target = -1
    private var alias: String? = null
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
        binding.llDeviceTag.isClickable = true
        binding.ivDeviceTagStatus.setImageResource(R.drawable.push_uncheck)
        getAlias()
        getAccount()
        binding.llDeviceTag.setOnClickListener {
            target = CloudPushService.DEVICE_TARGET
            binding.ivDeviceTagStatus.setImageResource(R.drawable.push_check)
            if (binding.llAliasTag.isClickable){
                binding.ivAliasTagStatus.setImageResource(R.drawable.push_check)
            }
            if (binding.llAccountTag.isClickable){
                binding.ivAccountTagStatus.setImageResource(R.drawable.push_check)
            }
        }

        binding.llAliasTag.setOnClickListener {

        }
        binding.llAccountTag.setOnClickListener {
            target = CloudPushService.ACCOUNT_TARGET
            binding.ivAccountTagStatus.setImageResource(R.drawable.push_check)
            if (binding.llDeviceTag.isClickable){
                binding.ivDeviceTagStatus.setImageResource(R.drawable.push_check)
            }
            if (binding.llAliasTag.isClickable){
                binding.ivAliasTagStatus.setImageResource(R.drawable.push_check)
            }
        }
    }

    private fun getAlias(){
        PushServiceFactory.getCloudPushService().listAliases(object : CommonCallback {
            override fun onSuccess(response: String?) {
                if (TextUtils.isEmpty(response)) {
                   binding.ivAliasTagStatus.setImageResource(R.drawable.push_unable_check)
                    binding.llAliasTag.isClickable = false
                }else {
                    binding.ivAliasTagStatus.setImageResource(R.drawable.push_uncheck)
                    binding.llAliasTag.isClickable = true
                }

            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                binding.ivAliasTagStatus.setImageResource(R.drawable.push_unable_check)
                binding.llAliasTag.isClickable = false
            }

        })
    }

    private fun getAccount(){
        val account = getSharedPreferences(
            "aliyun_push",
            Context.MODE_PRIVATE
        ).getString(SP_KEY_BIND_ACCOUNT, "")
        if (TextUtils.isEmpty(account)) {
            binding.ivAccountTagStatus.setImageResource(R.drawable.push_unable_check)
            binding.llAccountTag.isClickable = false
        }else {
            binding.ivAccountTagStatus.setImageResource(R.drawable.push_uncheck)
            binding.llAccountTag.isClickable = true
            binding.tvAccount.text = account
        }
    }

}


