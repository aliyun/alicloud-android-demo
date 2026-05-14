package com.aliyun.apm.android.demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aliyun.apm.android.demo.R
import com.aliyun.apm.android.demo.databinding.FragmentRemoteLogRecallSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal class RemoteLogRecallSheetFragment : BottomSheetDialogFragment() {

    companion object {
        private const val TAG = "RemoteLogRecallSheet"

        fun show(fragmentManager: androidx.fragment.app.FragmentManager) {
            RemoteLogRecallSheetFragment().show(fragmentManager, TAG)
        }
    }

    override fun getTheme(): Int = R.style.DemoBottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRemoteLogRecallSheetBinding.inflate(inflater, container, false)
        binding.btnAcknowledge.setOnClickListener { dismiss() }
        return binding.root
    }
}
