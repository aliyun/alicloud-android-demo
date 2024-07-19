package alibaba.httpdns_android_demo.function

import alibaba.httpdns_android_demo.KEY_HOST
import alibaba.httpdns_android_demo.search.SearchActivity
import alibaba.httpdns_android_demo.databinding.ResolveResultBinding
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

/**
 * 解析结果页面
 * @author 任伟
 * @date 2024/07/19
 */
class ResolveResultFragment:Fragment() {

    private var binding: ResolveResultBinding? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ResolveViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ResolveResultBinding.inflate(inflater , container , false)
        binding?.lifecycleOwner = viewLifecycleOwner
        viewModel?.initData(arguments)
        binding?.viewModel = viewModel
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.tvHost?.setOnClickListener {
            requestDataLauncher.launch(Intent(activity , SearchActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}