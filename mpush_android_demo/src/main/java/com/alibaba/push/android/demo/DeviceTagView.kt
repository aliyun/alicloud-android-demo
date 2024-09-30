package com.alibaba.push.android.demo

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.alibaba.push.android.demo.databinding.DeviceTagBinding

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

    var lookAllTag:(()->Unit)? = null

    init {
        binding = DeviceTagBinding.inflate(LayoutInflater.from(context), this, true)
        setPadding(0 , 0 , 0 , 8.toDp())
        binding.rvLabel.apply {
            addLabelClickCallback = {
                addDeviceTag()
            }
            deleteLabelClickCallback = {
                removeDeviceTag(it)
            }
            moreThanMaxLabelCountCallback = {
                binding.showAllBtn = it
            }
        }
        binding.tvAll.setOnClickListener {
            lookAllTag?.invoke()
        }
    }

    /**
     * 添加设备标签
     */
    private fun addDeviceTag(){
        DataSource.addDeviceTag(context) {
            binding.rvLabel.addLabel(it)
        }
    }

    /**
     * 移除设备标签
     */
    private fun removeDeviceTag(tag: String) {
        DataSource.removeDeviceTag(context, tag) {
            binding.rvLabel.deleteLabel(tag)
        }
    }

    /**
     * 更新设备标签
     */
    fun setDeviceTagData() {
        binding.rvLabel.setData(DataSource.getLabels(DataSource.LABEL_DEVICE_TAG))
    }

}