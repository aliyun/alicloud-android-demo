package alibaba.httpdns_android_demo.setting

import alibaba.httpdns_android_demo.R
import alibaba.httpdns_android_demo.databinding.HostListOperationBinding
import alibaba.httpdns_android_demo.showInputDialog
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * 预解析域名列表设置和清空指定域名缓存设置页面
 * @author 任伟
 * @date 2024/07/19
 */
class HostListOperationActivity : AppCompatActivity() {

    private lateinit var binding: HostListOperationBinding
    private lateinit var viewModel: HostListOperationViewModel
    private var adapter: HostListOperationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //状态栏设置
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        controller.isAppearanceLightStatusBars = true
        window.statusBarColor = Color.TRANSPARENT

        binding = HostListOperationBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[HostListOperationViewModel::class.java]
        binding.viewModel = viewModel
        viewModel.initData(intent)
        //初始化域名列表
        binding.rvHost.layoutManager = LinearLayoutManager(this)
        binding.rvHost.adapter =
            HostListOperationAdapter(viewModel.hosts, viewModel.selectHosts).apply {
                adapter = this
                //域名点击回调
                onItemClickListener = {
                    if (viewModel.selectHosts.contains(it)) {
                        viewModel.selectHosts.remove(it)
                    } else {
                        viewModel.selectHosts.add(it)
                    }
                    adapter?.notifyItemChanged(viewModel.hosts.indexOf(it))
                    viewModel.btnEnableClick.value = viewModel.selectHosts.size != 0
                }
            }
        binding.ivBack.setOnClickListener { finish() }
        binding.ivAddHost.setOnClickListener {
            showInputDialog(
                viewModel.addHostTitle,
                InputType.TYPE_CLASS_TEXT,
                getString(R.string.input_host)
            ) {
                if (viewModel.hosts.contains(it)) {
                    viewModel.hosts.remove(it)
                }
                viewModel.hosts.add(0, it)
                if (!viewModel.selectHosts.contains(it)) {
                    viewModel.selectHosts.add(it)
                }
                adapter?.notifyItemRangeChanged(0, viewModel.hosts.size)
                viewModel.btnEnableClick.value = true
                viewModel.saveHost(viewModel.hosts)
            }
        }

        binding.tvOperation.setOnClickListener {
            viewModel.operationBtnClick()
            Toast.makeText(
                this@HostListOperationActivity, if (viewModel.isPreHost) {
                    getString(R.string.pre_resolve_success)
                } else {
                    getString(R.string.cache_clear)
                }, Toast.LENGTH_SHORT
            ).show()
        }
    }
}