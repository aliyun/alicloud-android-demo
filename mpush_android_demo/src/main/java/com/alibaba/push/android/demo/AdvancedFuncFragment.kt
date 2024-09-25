package com.alibaba.push.android.demo

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.alibaba.push.android.demo.databinding.AdvancedFuncFragmentBinding
import com.alibaba.push.android.demo.databinding.CountLimitDialogBinding
import com.alibaba.push.android.demo.databinding.InputDialogBinding
import com.alibaba.sdk.android.push.CloudPushService
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * 高级功能Fragment
 * @author ren
 * @date 2024/09/20
 */
class AdvancedFuncFragment : Fragment() {

    private lateinit var binding: AdvancedFuncFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AdvancedFuncFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clBindPhone.setOnClickListener { showPhoneInputDialog() }
        val statusBarHeight = requireContext().getStatusBarHeight()
        binding.vStatusBg.layoutParams?.height = statusBarHeight
        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val alpha = if (scrollY * 1f / statusBarHeight > 1) {
                1f
            } else {
                scrollY * 1f / statusBarHeight
            }
            binding.vStatusBg.alpha = alpha
        }
        binding.tvCountLimit.setOnClickListener {
            showCountLimitDialog()
        }
    }

    private fun showCountLimitDialog(){
        val countLimitDialogBinding = CountLimitDialogBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = BottomSheetDialog(requireContext(), R.style.RoundedBottomSheetDialog).apply {
            setContentView(countLimitDialogBinding.root)
            countLimitDialogBinding.lifecycleOwner = this
            show()
        }
        countLimitDialogBinding.ivClose.setOnClickListener { dialog.dismiss() }
    }

    private fun showPhoneInputDialog() {
        context?.showInputDialog(R.string.push_text_notification, R.string.push_text_notification_hint,
            showAlert = false,
            showAliasInput = false) {it, _ ->
            bindPhone(it)
        }
    }

    private fun bindPhone(phone: String) {
        PushServiceFactory.getCloudPushService().bindPhoneNumber(phone, object:CommonCallback{
            override fun onSuccess(p0: String?) {
                binding.tvPhone.text = phone
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {
                context?.toast(R.string.push_toast_bind_phone_fail, errorMessage)
            }
        })
    }


}