package alibaba.httpdns_android_demo

import alibaba.httpdns_android_demo.databinding.FragmentSettingBinding
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class SettingFragment:Fragment(),ITimeoutSettingDialog {

    private var binding:FragmentSettingBinding? = null
    private lateinit var viewModel: SettingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        viewModel.timeoutDialog = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater , container , false)
        binding?.lifecycleOwner = this
        viewModel.initData()
        binding?.viewModel = viewModel
        return binding?.root
    }

    override fun show() {
        context?.showInputDialog("超时时间" , InputType.TYPE_CLASS_NUMBER) {
            viewModel.saveTimeout(it.toInt())
        }
    }

}