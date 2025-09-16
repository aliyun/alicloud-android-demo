package com.alibaba.httpdns.android.demo.search

import alibaba.httpdns_android_demo.databinding.SearchBinding
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.httpdns.android.demo.KEY_HOST

/**
 * 搜索Activity
 * @author renwei
 * @date 2024/07/19
 */
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: SearchBinding
    private lateinit var viewModel: SearchViewModel
    private var inputHistoryAdapter: SearchHostListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        //状态栏浸入
        controller.isAppearanceLightStatusBars = true
        //状态栏透明
        window.statusBarColor = Color.TRANSPARENT

        binding = SearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        binding.viewModel = viewModel

        //初始化输入历史列表
        binding.rvInputHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvInputHistory.adapter = SearchHostListAdapter(viewModel.inputHistory).apply {
            inputHistoryAdapter = this
            //域名删除回调
            onItemDeleteListener = { host, position ->
                viewModel.inputHistory.remove(host)
                inputHistoryAdapter?.notifyItemRemoved(position)
                inputHistoryAdapter?.notifyItemChanged(position)
                viewModel.inputHistoryEnableVisible.value = viewModel.inputHistory.isNotEmpty()
                viewModel.saveInputHistory()
            }
            //域名点击回调
            onItemClickListener = ::callbackHost
        }

        //初始化控制台域名列表
        binding.rvControlHost.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvControlHost.adapter = SearchHostListAdapter(viewModel.controlHost).apply {
            hideClose = true
            onItemClickListener = ::callbackHost
        }

        //键盘搜索点击回调
        binding.etInputHost.setOnEditorActionListener { _, actionId, _ ->
            val host = binding.etInputHost.text.toString().trim()
            if (actionId == EditorInfo.IME_ACTION_SEARCH && !TextUtils.isEmpty(host)) {
                if (!viewModel.inputHistory.contains(host)) {
                    viewModel.inputHistory.add(0, host)
                    inputHistoryAdapter?.notifyItemInserted(0)
                    inputHistoryAdapter?.notifyItemChanged(0)
                    viewModel.saveInputHistory()
                }
                callbackHost(host)
                true
            } else {
                false
            }
        }
        binding.ivBack.setOnClickListener { finish() }

        binding.tvClear.setOnClickListener {
            viewModel.clearInputHistory()
            inputHistoryAdapter?.notifyItemRangeChanged(0, viewModel.inputHistory.size)
        }
    }

    /**
     * 域名回传
     */
    private fun callbackHost(host: String) {
        setResult(RESULT_OK, Intent().apply {
            putExtra(KEY_HOST, host)
        })
        finish()
    }
}