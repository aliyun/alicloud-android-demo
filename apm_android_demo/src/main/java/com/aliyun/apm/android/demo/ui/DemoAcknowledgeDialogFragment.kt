package com.aliyun.apm.android.demo.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.aliyun.apm.android.demo.R
import com.aliyun.apm.android.demo.databinding.DialogAcknowledgeBinding

internal class DemoAcknowledgeDialogFragment : DialogFragment() {

    companion object {
        private const val TAG = "DemoAcknowledgeDialog"
        private const val ARG_TITLE = "title"
        private const val ARG_MESSAGE = "message"

        fun show(
            fragmentManager: androidx.fragment.app.FragmentManager,
            title: String,
            message: String
        ) {
            val fragment = DemoAcknowledgeDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_MESSAGE, message)
                }
            }
            fragment.show(fragmentManager, TAG)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            requestFeature(Window.FEATURE_NO_TITLE)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogAcknowledgeBinding.inflate(inflater, container, false)
        binding.root.setBackgroundResource(R.drawable.bg_dialog)
        binding.root.clipToOutline = true

        binding.dialogTitle.text = arguments?.getString(ARG_TITLE) ?: ""
        binding.dialogMessage.text = arguments?.getString(ARG_MESSAGE) ?: ""

        binding.btnAcknowledge.setOnClickListener { dismiss() }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val margin = resources.getDimensionPixelSize(R.dimen.demo_spacing_dialog_horizontal_margin)
            setLayout(
                resources.displayMetrics.widthPixels - margin * 2,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}
