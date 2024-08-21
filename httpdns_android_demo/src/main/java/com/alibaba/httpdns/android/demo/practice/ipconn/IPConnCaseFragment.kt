package com.alibaba.httpdns.android.demo.practice.ipconn

import com.alibaba.httpdns.android.demo.BaseFragment
import alibaba.httpdns_android_demo.R
import alibaba.httpdns_android_demo.databinding.IPConnCaseBinding
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider

/**
 * IP直连最佳实践页面
 * @author 任伟
 * @date 2024/07/22
 */
class IPConnCaseFragment : BaseFragment<IPConnCaseBinding>() {

    private lateinit var viewModel: IPConnCaseViewModel

    override fun getLayoutId(): Int {
        return R.layout.httpdns_fragment_ip_conn_case
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[IPConnCaseViewModel::class.java]
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
    }
}