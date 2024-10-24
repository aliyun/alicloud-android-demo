package com.alibaba.push.android.demo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.push.android.demo.databinding.AllLabelActivityBinding

class AllLabelActivity : AppCompatActivity() {

    private lateinit var binding: AllLabelActivityBinding
    private lateinit var viewModel: AdvanceFuncViewModel

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
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[AdvanceFuncViewModel::class.java]
        viewModel.initData()
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
            labelAdapter = AllLabelAdapter(mutableListOf()).apply {
                deleteLabelCallback = {

                }
            }
            adapter = labelAdapter
        }

        binding.ivAdd.setOnClickListener {

        }

        binding.ivBack.setOnClickListener {
            setResult(RESULT_OK, Intent().apply {
                putExtra("type", labelType)
            })
            finish()
        }

    }


    override fun onBackPressed() {
        setResult(RESULT_OK, Intent().apply {
            putExtra("type", labelType)
        })
        finish()
    }

}