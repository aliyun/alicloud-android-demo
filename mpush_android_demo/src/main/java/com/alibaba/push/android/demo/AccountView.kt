package com.alibaba.push.android.demo

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.alibaba.push.android.demo.databinding.AccountBinding
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory

/**
 * 账号设置View
 * @author ren
 * @date 2024/09/26
 */
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
                addAccountTag()
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

    /**
     * 添加账号标签
     */
    private fun addAccountTag() {
        DataSource.addAccountTag(context) {
            binding.rvLabelTag.addLabel(it)
        }
    }

    /**
     * 移除账号标签
     */
    private fun deleteAccountTag(tag: String) {
        DataSource.removeAccountTag(context, tag) {
            binding.rvLabelTag.deleteLabel(tag)
        }
    }

    /**
     * 弹窗账号输入弹窗
     */
    private fun showAccountInputDialog() {
        context.showInputDialog(
            R.string.push_bind_account, R.string.push_bind_account_hint,
            showAlert = false,
            showAliasInput = false
        ) { it, _ ->
            bindAccount(it)
        }
    }

    /**
     * 绑定账号
     */
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

    /**
     * 更新账号标签
     */
    fun setAccountTagData() {
        binding.rvLabelTag.setData(DataSource.getLabels(DataSource.LABEL_ACCOUNT_TAG))
    }
}