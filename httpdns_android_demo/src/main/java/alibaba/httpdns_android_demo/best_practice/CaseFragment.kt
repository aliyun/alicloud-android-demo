package alibaba.httpdns_android_demo.best_practice

import alibaba.httpdns_android_demo.BaseFragment
import alibaba.httpdns_android_demo.R
import alibaba.httpdns_android_demo.databinding.CaseBinding
import alibaba.httpdns_android_demo.view.CaseItemDecoration
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * 最佳实践页面
 * @author 任伟
 * @date 2024/07/22
 */
class CaseFragment:BaseFragment<CaseBinding>() {

    companion object {
        val caseData:List<Pair<Int,String>> =mutableMapOf<Int, String>().apply {
            put(R.id.navigation_okhttp , "OkHttp\n网络库\n最佳实践")
            put(R.id.navigation_webview , "HTTPDNS\nWebview\n最佳实践")
            put(R.id.navigation_ip_conn , "HTTPDNS\nIP直连\n最佳实践")
            put(R.id.navigation_exo_player , "HTTPDNS\nExoPlayer\n最佳实践")
        }.toList()
    }

    private lateinit var viewModel:CaseViewModel

    override fun getLayoutId(): Int {
        return R.layout.httpdns_fragment_case
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CaseViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvCase.layoutManager = LinearLayoutManager(context , LinearLayoutManager.HORIZONTAL , false)
        binding.rvCase.addItemDecoration(CaseItemDecoration())
        binding.rvCase.adapter = CaseAdapter(caseData).apply{
            onItemClickListener = ::changeFragment
            selectItem = viewModel.currCaseIndex.value?:0
        }
    }

    /**
     * 最佳实践fragment切换
     */
    private fun changeFragment(id: Int , position:Int) {
        viewModel.currCaseIndex.value = position
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_case) as NavHostFragment
        navHostFragment.navController.navigate(id)
    }
}