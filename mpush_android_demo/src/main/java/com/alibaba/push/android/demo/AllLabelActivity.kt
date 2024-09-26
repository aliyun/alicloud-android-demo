package com.alibaba.push.android.demo

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.push.android.demo.databinding.AllLabelActivityBinding

class AllLabelActivity: AppCompatActivity()  {

    private lateinit var binding: AllLabelActivityBinding

    private var labelType:Int = 0

    private var labelAdapter:AllLabelAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        //状态栏浸入
        controller.isAppearanceLightStatusBars = true
        //状态栏透明
        window.statusBarColor = Color.TRANSPARENT
        binding = AllLabelActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        labelType = intent.getIntExtra("type" , 0)
        binding.title = when(labelType) {
            DataSource.LABEL_DEVICE_TAG -> getString(R.string.push_device_tag)
            DataSource.LABEL_ALIAS -> getString(R.string.push_alias_set)
            DataSource.LABEL_ALIAS_TAG->getString(R.string.push_alias_tag)
            else -> getString(R.string.push_account_tag)
        }
        binding.rvLabel.apply {
            layoutManager = GridLayoutManager(this@AllLabelActivity, 3, RecyclerView.VERTICAL, false)
            addItemDecoration(GridSpacingItemDecoration(3, 8.toDp()))
            labelAdapter = AllLabelAdapter(DataSource.getLabels(labelType)).apply {
                deleteLabelCallback = {
                    deleteLabel(it)
                }
            }
            adapter = labelAdapter
        }

    }

    private fun deleteLabel(tag: String) {

    }

}