package com.alibaba.push.android.demo

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.alibaba.push.android.demo.databinding.AliasSetBinding
import com.alibaba.sdk.android.push.CloudPushService
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory

/**
 * 别名设置视图
 * @author ren
 * @date 2024/09/25
 */
class AliasSetView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: AliasSetBinding
    private val aliasTagMap = mutableMapOf<String,String>()

    init {
        binding = AliasSetBinding.inflate(LayoutInflater.from(context), this, true)
        setPadding(0 , 0 , 0 , 8.toDp())
        binding.rvLabel.apply {
            addLabelClickCallback = {
                showAddAliasDialog()
            }
            deleteLabelClickCallback = {
                deleteAlias(it)
            }
            moreThanMaxLabelCountCallback = {
                binding.showAliasAllBtn = it
            }
        }

        binding.rvLabelTag.apply {
            addLabelClickCallback = {
                showAddAliasTagDialog()
            }
            deleteLabelClickCallback = {
                deleteAliasTag(it)
            }
            moreThanMaxLabelCountCallback = {
                binding.showAliasTagAllBtn = it
            }
        }

    }

    private fun deleteAliasTag(tag: String) {
        PushServiceFactory.getCloudPushService().unbindTag(CloudPushService.ALIAS_TARGET, arrayOf(tag), aliasTagMap[tag], object:CommonCallback{
            override fun onSuccess(p0: String?) {
                binding.rvLabelTag.addLabel(tag)
                aliasTagMap.remove(tag)
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                context.toast(String.format(context.getString(R.string.push_toast_delete_alias_tag_fail), errorMessage))
            }

        })
    }

    private fun showAddAliasTagDialog() {
        context.showInputDialog(R.string.push_add_alias_tag, R.string.push_input_alias_tag_hint,
            showAlert = true,
            showAliasInput = true
        ) { it, alias ->
            if (TextUtils.isEmpty(alias)) {
                context.toast(context.getString(R.string.push_alias_empty))
                return@showInputDialog
            }
            addAliasTag(it, alias!!)
        }
    }

    private fun addAliasTag(tag: String, alias: String){
        PushServiceFactory.getCloudPushService().bindTag(CloudPushService.ALIAS_TARGET, arrayOf(tag), alias, object:CommonCallback{
            override fun onSuccess(p0: String?) {
                binding.rvLabelTag.addLabel(tag)
                aliasTagMap[tag] = alias
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                context.toast(String.format(context.getString(R.string.push_toast_add_alias_tag_fail), errorMessage))
            }

        })
    }


    /**
     * 删除别名
     */
    private fun deleteAlias(alias: String) {
        PushServiceFactory.getCloudPushService().removeAlias(alias, object: CommonCallback{
            override fun onSuccess(p0: String?) {
                binding.rvLabel.deleteLabel(alias)
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                context.toast(String.format(context.getString(R.string.push_toast_delete_alias_fail), errorMessage))
            }
        })
    }

    /**
     * 弹出别名输入弹窗
     */
    private fun showAddAliasDialog(){
        context.showInputDialog(R.string.push_add_alias, R.string.push_input_alias_hint,
            showAlert = false,
            showAliasInput = false
        ) { it, _ ->
            addAlias(it)
        }
    }

    /**
     * 添加别名
     */
    private fun addAlias(alias: String){
        PushServiceFactory.getCloudPushService().addAlias(alias,object:CommonCallback{
            override fun onSuccess(p0: String?) {
                binding.rvLabel.addLabel(alias)
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                context.toast(String.format(context.getString(R.string.push_toast_add_alias_fail), errorMessage))
            }

        })
    }



}