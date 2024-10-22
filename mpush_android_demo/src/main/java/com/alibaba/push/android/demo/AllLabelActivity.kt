package com.alibaba.push.android.demo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.push.android.demo.databinding.AllLabelActivityBinding

class AllLabelActivity : AppCompatActivity() {

    private lateinit var binding: AllLabelActivityBinding

    private var labelType: Int = 0

    private var labelAdapter: AllLabelAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        //状态栏浸入
        controller.isAppearanceLightStatusBars = true
        //状态栏透明
        window.statusBarColor = Color.TRANSPARENT

        binding = AllLabelActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        labelType = intent.getIntExtra("type", 0)
        binding.title = when (labelType) {
            DataSource.LABEL_DEVICE_TAG -> getString(R.string.push_device_tag)
            DataSource.LABEL_ALIAS -> getString(R.string.push_alias_set)
            DataSource.LABEL_ALIAS_TAG -> getString(R.string.push_alias_tag)
            else -> getString(R.string.push_account_tag)
        }
        binding.rvLabel.apply {
            layoutManager =
                GridLayoutManager(this@AllLabelActivity, 3, RecyclerView.VERTICAL, false)
            addItemDecoration(GridSpacingItemDecoration(3, 8.toDp()))
            labelAdapter = AllLabelAdapter(mutableListOf<String>().apply {
                addAll(DataSource.getLabels(labelType))
            }).apply {
                deleteLabelCallback = {
                    deleteTag(it)
                }
            }
            adapter = labelAdapter
        }

        binding.ivAdd.setOnClickListener {
            addTag()
        }

        binding.ivBack.setOnClickListener {
            setResult(RESULT_OK, Intent().apply {
                putExtra("type", labelType)
            })
            finish()
        }

    }

    private fun addTag() {
        when (labelType) {
            DataSource.LABEL_DEVICE_TAG -> DataSource.addDeviceTag(this, ::addLabel)
            DataSource.LABEL_ALIAS -> DataSource.addAlias(this, ::addLabel)
            DataSource.LABEL_ALIAS_TAG -> DataSource.addAliasTag(this, ::addLabel)
            else -> DataSource.addAccountTag(this, ::addLabel)
        }
    }

    private fun addLabel(label: String) {
        labelAdapter?.labels?.add(0, label)
        labelAdapter?.notifyDataSetChanged()
    }

    private fun deleteTag(tag: String) {
        when (labelType) {
            DataSource.LABEL_DEVICE_TAG -> DataSource.removeDeviceTag(this, tag, ::deleteLabel)
            DataSource.LABEL_ALIAS -> DataSource.removeAlias(this, tag, ::deleteLabel)
            DataSource.LABEL_ALIAS_TAG -> DataSource.removeAliasTag(this, tag, ::deleteLabel)
            else -> DataSource.removeAccountTag(this, tag, ::deleteLabel)
        }
    }

    private fun deleteLabel(label: String) {
        labelAdapter?.labels?.remove(label)
        labelAdapter?.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        setResult(RESULT_OK, Intent().apply {
            putExtra("type", labelType)
        })
        finish()
    }

}