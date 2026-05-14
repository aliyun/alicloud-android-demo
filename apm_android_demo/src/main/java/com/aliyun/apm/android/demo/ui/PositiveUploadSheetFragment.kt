package com.aliyun.apm.android.demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aliyun.apm.android.demo.R
import com.aliyun.apm.android.demo.databinding.FragmentPositiveUploadSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal class PositiveUploadSheetFragment : BottomSheetDialogFragment() {

    companion object {
        private const val TAG = "PositiveUploadSheet"

        fun show(fragmentManager: androidx.fragment.app.FragmentManager) {
            PositiveUploadSheetFragment().show(fragmentManager, TAG)
        }
    }

    override fun getTheme(): Int = R.style.DemoBottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPositiveUploadSheetBinding.inflate(inflater, container, false)
        binding.btnAcknowledge.setOnClickListener { dismiss() }
        return binding.root
    }
}
