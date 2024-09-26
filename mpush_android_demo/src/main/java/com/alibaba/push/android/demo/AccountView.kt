package com.alibaba.push.android.demo

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.alibaba.push.android.demo.databinding.AccountBinding
import com.alibaba.sdk.android.push.CloudPushService
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory

class AccountView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: AccountBinding
    var lookAllTag: (() -> Unit)? = null

    init {
        binding = AccountBinding.inflate(LayoutInflater.from(context), this, true)
        binding.tvTitle.setOnClickListener {
            showAccountInputDialog()
        }
        binding.rvLabelTag.apply {
            addLabelClickCallback = {
                showAccountTagInputDialog()
            }
            deleteLabelClickCallback = {
                deleteAccountTag(it)
            }
            moreThanMaxLabelCountCallback = {
                binding.showAllBtn = it
            }
        }
        binding.tvAllTag.setOnClickListener {
            lookAllTag?.invoke()
        }
    }

    private fun deleteAccountTag(tag: String) {
        PushServiceFactory.getCloudPushService().unbindTag(
            CloudPushService.ACCOUNT_TARGET,
            arrayOf(tag),
            null,
            object : CommonCallback {
                override fun onSuccess(p0: String?) {
                    binding.rvLabelTag.deleteLabel(tag)
                }

                override fun onFailed(errorCode: String?, errorMessage: String?) {
                    context.toast(R.string.push_unbind_account_tag_fail, errorMessage)
                }
            })
    }

    private fun showAccountTagInputDialog() {
        context.showInputDialog(
            R.string.push_bind_account_tag, R.string.push_bind_account_tag_hint,
            showAlert = true,
            showAliasInput = false
        ) { it, _ ->
            bindAccountTag(it)
        }
    }

    private fun bindAccountTag(tag: String) {
        PushServiceFactory.getCloudPushService()
            .bindTag(CloudPushService.ACCOUNT_TARGET, arrayOf(tag), null, object : CommonCallback {
                override fun onSuccess(p0: String?) {
                    binding.rvLabelTag.addLabel(tag)
                }

                override fun onFailed(errorCode: String?, errorMessage: String?) {
                    context.toast(R.string.push_bind_account_tag_fail, errorMessage)
                }

            })
    }


    private fun showAccountInputDialog() {
        context.showInputDialog(
            R.string.push_bind_account, R.string.push_bind_account_hint,
            showAlert = false,
            showAliasInput = false
        ) { it, _ ->
            bindAccount(it)
        }
    }

    private fun bindAccount(account: String) {
        PushServiceFactory.getCloudPushService().bindAccount(account, object : CommonCallback {
            override fun onSuccess(p0: String?) {
                binding.tvAccount.text = account
                binding.alreadyBindAccount = true
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                context.toast(R.string.push_bind_account_fail, errorMessage)
            }

        })
    }

    fun setAccountTagData(data: MutableList<String>) {
        binding.rvLabelTag.setData(data)
    }

}