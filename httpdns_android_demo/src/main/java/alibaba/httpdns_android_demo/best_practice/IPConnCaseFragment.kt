package alibaba.httpdns_android_demo.best_practice

import alibaba.httpdns_android_demo.BaseFragment
import alibaba.httpdns_android_demo.R
import alibaba.httpdns_android_demo.databinding.IPConnCaseBinding
import alibaba.httpdns_android_demo.showOkHttpResponseAlert
import android.os.Bundle
import android.text.TextUtils
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
        requireContext().showOkHttpResponseAlert(
            getString(R.string.response_title),
            viewModel.responseStr.value ?: ""
        )
    }

}