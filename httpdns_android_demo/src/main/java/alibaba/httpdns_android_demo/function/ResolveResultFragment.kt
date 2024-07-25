package alibaba.httpdns_android_demo.function

import alibaba.httpdns_android_demo.BaseFragment
import alibaba.httpdns_android_demo.KEY_HOST
import alibaba.httpdns_android_demo.R
import alibaba.httpdns_android_demo.search.SearchActivity
import alibaba.httpdns_android_demo.databinding.ResolveResultBinding
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider

/**
 * 解析结果页面
 * @author 任伟
 * @date 2024/07/19
 */
class ResolveResultFragment:BaseFragment<ResolveResultBinding>() {

    private var viewModel: ResolveViewModel? = null

    /**
     * 注册页面跳转回传数据的监听
     */
    private val requestDataLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel?.host?.value = result.data?.getStringExtra(KEY_HOST)
            viewModel?.resolve()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.httpdns_fragment_resolve_result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ResolveViewModel::class.java]
        viewModel?.initData(arguments)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.tvHost.setOnClickListener {
            requestDataLauncher.launch(Intent(activity , SearchActivity::class.java))
        }
    }

}