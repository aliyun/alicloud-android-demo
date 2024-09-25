package com.alibaba.push.android.demo

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.alibaba.push.android.demo.databinding.DeviceTagBinding
import com.alibaba.sdk.android.push.CloudPushService
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory

/**
 * 设备标签视图
 * @author ren
 * @date 2024/09/25
 */
class DeviceTagView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: DeviceTagBinding

    init {
        binding = DeviceTagBinding.inflate(LayoutInflater.from(context), this, true)
        setPadding(0 , 0 , 0 , 8.toDp())
        binding.rvLabel.apply {
            addLabelClickCallback = {
                showTagInputDialog()
            }
            deleteLabelClickCallback = {
                deleteTag(it)
            }
            moreThanMaxLabelCountCallback = {
                binding.showAllBtn = it
            }
        }
    }

    private fun deleteTag(tag: String) {
        PushServiceFactory.getCloudPushService().unbindTag(CloudPushService.DEVICE_TARGET , arrayOf(tag), null, object:CommonCallback{
            override fun onSuccess(p0: String?) {
                binding.rvLabel.deleteLabel(tag)
            }

            override fun onFailed(erroCode: String?, errorMessage: String?) {
                context.toast(R.string.push_toast_delete_device_tag_fail, errorMessage)
            }

        })
    }

    private fun showTagInputDialog() {
        context.showInputDialog(R.string.push_add_device_tag, R.string.push_input_device_tag_hint,
            showAlert = true,
            showAliasInput = false) {it,_ ->
            addTag(it)
        }
    }

    private fun addTag(tag: String) {
        PushServiceFactory.getCloudPushService().bindTag(CloudPushService.DEVICE_TARGET , arrayOf(tag), null, object:CommonCallback{
            override fun onSuccess(p0: String?) {
                binding.rvLabel.addLabel(tag)
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                context.toast(R.string.push_toast_add_device_tag_fail, errorMessage)
            }

        })
    }

}