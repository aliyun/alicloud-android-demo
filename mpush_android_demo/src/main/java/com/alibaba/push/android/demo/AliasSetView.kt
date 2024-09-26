package com.alibaba.push.android.demo

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.alibaba.push.android.demo.databinding.AliasSetBinding

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

    var lookAllTag: ((Int) -> Unit)? = null

    init {
        binding = AliasSetBinding.inflate(LayoutInflater.from(context), this, true)
        setPadding(0, 0, 0, 8.toDp())
        binding.rvLabel.apply {
            addLabelClickCallback = {
                addAlias()
            }
            deleteLabelClickCallback = {
                removeAlias(it)
            }
            moreThanMaxLabelCountCallback = {
                binding.showAliasAllBtn = it
            }
        }

        binding.rvLabelTag.apply {
            addLabelClickCallback = {
                addAliasTag()
            }
            deleteLabelClickCallback = {
                removeAliasTag(it)
            }
            moreThanMaxLabelCountCallback = {
                binding.showAliasTagAllBtn = it
            }
        }

        binding.tvAll.setOnClickListener {
            lookAllTag?.invoke(DataSource.LABEL_ALIAS)
        }

        binding.tvAllTag.setOnClickListener {
            lookAllTag?.invoke(DataSource.LABEL_ALIAS_TAG)
        }
    }

    /**
     * 添加别名
     */
    private fun addAlias() {
        DataSource.addAlias(context) {
            binding.rvLabel.addLabel(it)
        }
    }

    /**
     * 移除别名
     */
    private fun removeAlias(alias: String) {
        DataSource.removeAlias(context, alias) {
            binding.rvLabel.deleteLabel(alias)
        }
    }

    /**
     * 添加别名标签
     */
    private fun addAliasTag() {
        DataSource.addAliasTag(context) {
            binding.rvLabelTag.addLabel(it)
        }
    }

    /**
     * 移除别名标签
     */
    private fun removeAliasTag(tag: String) {
        DataSource.removeAliasTag(context, tag) {
            binding.rvLabelTag.addLabel(tag)
        }
    }

    /**
     * 更新别名数据
     */
    fun setAliasData(data: MutableList<String>) {
        binding.rvLabel.setData(data)
    }

    /**
     * 更新别名标签数据
     */
    fun setAliasTagData(data: MutableList<String>) {
        binding.rvLabelTag.setData(data)
    }
}