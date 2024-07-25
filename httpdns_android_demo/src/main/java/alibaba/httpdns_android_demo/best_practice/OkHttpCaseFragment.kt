package alibaba.httpdns_android_demo.best_practice

import alibaba.httpdns_android_demo.BaseFragment
import alibaba.httpdns_android_demo.R
import alibaba.httpdns_android_demo.databinding.OkHttpCaseBinding
import alibaba.httpdns_android_demo.showOkHttpResponseAlert
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProvider

/**
 * OkHttp最佳实践页面
 * @author 任伟
 * @date 2024/07/22
 */
class OkHttpCaseFragment: BaseFragment<OkHttpCaseBinding>() {

    private lateinit var viewModel:OkHttpCaseViewModel

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
        binding.tvRequestResult.setOnClickListener {
            showRequestResultDialog()
        }
    }

    /**
     * 展示OkHttp请求结果
     */
    private fun showRequestResultDialog() {
        val code = viewModel.response?.code
        val body = viewModel.response?.body
        val responseStr = if (code == 200 && !TextUtils.isEmpty(body)) {
            if (body!!.length <= 100) "$code - $body" else "$code - ${getString(R.string.body_large_see_log)}"
        } else {
            code.toString()
        }
        requireContext().showOkHttpResponseAlert(getString(R.string.response_title) , responseStr)
    }

}