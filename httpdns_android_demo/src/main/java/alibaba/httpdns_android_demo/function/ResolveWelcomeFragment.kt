package alibaba.httpdns_android_demo.function

import alibaba.httpdns_android_demo.BaseFragment
import alibaba.httpdns_android_demo.KEY_HOST
import alibaba.httpdns_android_demo.KEY_RESOLVE_API_TYPE
import alibaba.httpdns_android_demo.R
import alibaba.httpdns_android_demo.search.SearchActivity
import alibaba.httpdns_android_demo.databinding.WelcomeBinding
import alibaba.httpdns_android_demo.showHostResolveAlert
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController

/**
 * 解析欢迎页
 * @author 任伟
 * @date 2024/07/19
 */
class ResolveWelcomeFragment: BaseFragment<WelcomeBinding>() {

    private var viewModel: ResolveViewModel? = null

    /**
     * 处理页面跳转,返回数据的回调处理
     */
    private val requestDataLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        if (result.resultCode == RESULT_OK) {
            viewModel?.host?.value = result.data?.getStringExtra(KEY_HOST)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.httpdns_fragment_resolve_welcome
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ResolveViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.tvHost.setOnClickListener {
            val intent = Intent(activity , SearchActivity::class.java)
            requestDataLauncher.launch(intent)
        }

        binding.tvResolve.setOnClickListener {
            requireContext().showHostResolveAlert(viewModel?.host?.value?:"") {
                findNavController(requireActivity() , R.id.nav_fragment_function)
                    .navigate(R.id.action_to_resolve_result, Bundle().apply {
                        putString(KEY_HOST , viewModel?.host?.value)
                        putInt(KEY_RESOLVE_API_TYPE , viewModel?.currResolveApiType?.value?: ResolveApiType.RESOLVE_SYNC)
                    })
            }
        }
    }
}