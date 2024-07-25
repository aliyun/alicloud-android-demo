package alibaba.httpdns_android_demo.setting

import alibaba.httpdns_android_demo.BaseFragment
import alibaba.httpdns_android_demo.KEY_IS_PRE_HOST
import alibaba.httpdns_android_demo.R
import alibaba.httpdns_android_demo.databinding.PopupRegionSettingBinding
import alibaba.httpdns_android_demo.databinding.SettingBinding
import alibaba.httpdns_android_demo.getStatusBarHeight
import alibaba.httpdns_android_demo.showInputDialog
import alibaba.httpdns_android_demo.toDp
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.lifecycle.ViewModelProvider

/**
 * 设置页面
 * @author 任伟
 * @date 2024/07/19
 */
class SettingFragment: BaseFragment<SettingBinding>(), ITimeoutSettingDialog, IRegionPopup {

    private lateinit var viewModel: SettingViewModel

    private var regionSettingPopup:PopupWindow? = null

    override fun getLayoutId(): Int {
        return R.layout.httpdns_fragment_setting
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        viewModel.timeoutDialog = this
        viewModel.regionPopup = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initData()
        viewModel.timeoutDialog = this
        viewModel.regionPopup = this

        binding.viewModel = viewModel
        binding.civHostPreResolve.setOnClickListener {
            gotoHostListOperation(true)
        }
        binding.civClearHostCache.setOnClickListener {
            gotoHostListOperation(false)
        }
        //滚动距离和状态栏透明度的联动
        val statusBarHeight = requireContext().getStatusBarHeight()
        binding.vStatusBg.layoutParams?.height = statusBarHeight
        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val alpha = if (scrollY*1f/statusBarHeight > 1) {
                1f
            }else {
                scrollY*1f/statusBarHeight
            }
            binding.vStatusBg.alpha = alpha
        }
        binding.clAboutUs.setOnClickListener { startActivity(Intent(context , AboutUsActivity::class.java)) }
    }

    /**
     * 跳转预解析域名列表设置页和清空指定域名缓存设置页
     */
    private fun gotoHostListOperation(isPreHost:Boolean){
        startActivity(Intent(context , HostListOperationActivity::class.java).apply {
            putExtra(KEY_IS_PRE_HOST , isPreHost)
        })
    }

    /**
     * 弹出超时设置弹窗
     */
    override fun show() {
        context?.showInputDialog("超时时间" , InputType.TYPE_CLASS_NUMBER) {
            viewModel.saveTimeout(it.toInt())
        }
    }

    /**
     * 弹出设置region的弹窗
     */
    override fun showRegionPopup(view:View) {
        if (regionSettingPopup == null) {
            val binding = PopupRegionSettingBinding.inflate(LayoutInflater.from(context) , null , false)
            binding.viewModel = viewModel
            binding.lifecycleOwner = viewLifecycleOwner
            regionSettingPopup = PopupWindow(binding.root)
            regionSettingPopup?.width =  ViewGroup.LayoutParams.WRAP_CONTENT
            regionSettingPopup?.height = 196.toDp()
            regionSettingPopup?.isTouchable = true
            regionSettingPopup?.isFocusable = true
            regionSettingPopup?.isOutsideTouchable = true
        }
        regionSettingPopup?.showAsDropDown(view)
    }

    override fun hideRegionPopup() {
        regionSettingPopup?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        regionSettingPopup = null
    }
}