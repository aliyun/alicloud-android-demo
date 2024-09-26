package com.alibaba.httpdns.android.demo.practice.okhttp

import com.alibaba.httpdns.android.demo.BaseFragment
import alibaba.httpdns_android_demo.R
import alibaba.httpdns_android_demo.databinding.OkHttpCaseBinding
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider

/**
 * OkHttp最佳实践页面
 * @author 任伟
 * @date 2024/07/22
 */
class OkHttpCaseFragment : BaseFragment<OkHttpCaseBinding>() {

    private lateinit var viewModel: OkHttpCaseViewModel

    override fun getLayoutId(): Int {
        return R.layout.httpdns_fragment_okhttp_case
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[OkHttpCaseViewModel::class.java]
        viewModel.initData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
    }
}