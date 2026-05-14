package com.aliyun.apm.android.demo.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.aliyun.apm.android.demo.R
import com.aliyun.apm.android.demo.databinding.DialogAdvancedAcknowledgeBinding

internal class DemoAdvancedAcknowledgeDialogFragment : DialogFragment() {

    companion object {
        private const val TAG = "DemoAdvancedAckDialog"
        private const val ARG_TITLE = "title"
        private const val ARG_SECTION_TITLES = "section_titles"
        private const val ARG_SECTION_DESCRIPTIONS = "section_descriptions"

        fun show(
            fragmentManager: androidx.fragment.app.FragmentManager,
            title: String,
            sections: List<AdvancedAcknowledgeSection>
        ) {
            val fragment = DemoAdvancedAcknowledgeDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putStringArrayList(ARG_SECTION_TITLES, ArrayList(sections.map { it.title }))
                    putStringArrayList(ARG_SECTION_DESCRIPTIONS, ArrayList(sections.map { it.description }))
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
        val binding = DialogAdvancedAcknowledgeBinding.inflate(inflater, container, false)
        binding.root.setBackgroundResource(R.drawable.bg_dialog)
        binding.root.clipToOutline = true

        binding.dialogTitle.text = arguments?.getString(ARG_TITLE) ?: ""

        val titles = arguments?.getStringArrayList(ARG_SECTION_TITLES) ?: emptyList()
        val descriptions = arguments?.getStringArrayList(ARG_SECTION_DESCRIPTIONS) ?: emptyList()

        val itemToDescGap = resources.getDimensionPixelSize(R.dimen.demo_spacing_dialog_structured_item_to_description)
        val descToItemGap = resources.getDimensionPixelSize(R.dimen.demo_spacing_dialog_structured_description_to_item)

        titles.forEachIndexed { index, sectionTitle ->
            val titleView = TextView(requireContext()).apply {
                setTextAppearance(R.style.DemoTextStyle_AdvancedDialogSectionTitle)
                text = sectionTitle
                includeFontPadding = false
                if (index > 0) {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = descToItemGap
                    }
                }
            }
            binding.sectionsContainer.addView(titleView)

            if (index < descriptions.size) {
                val descView = TextView(requireContext()).apply {
                    setTextAppearance(R.style.DemoTextStyle_AdvancedDialogSectionDescription)
                    text = descriptions[index]
                    includeFontPadding = false
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = itemToDescGap
                    }
                }
                binding.sectionsContainer.addView(descView)
            }
        }

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
