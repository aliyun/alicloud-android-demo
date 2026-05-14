package com.aliyun.apm.android.demo.internal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aliyun.apm.android.demo.DemoBusinessActions
import com.aliyun.apm.android.demo.DemoBusinessActions.DEFAULT_NETWORK_REQUEST_URL
import com.aliyun.apm.android.demo.DemoBusinessActions.NETWORK_LIBRARY_HTTP_URL_CONNECTION
import com.aliyun.apm.android.demo.DemoBusinessActions.NETWORK_LIBRARY_OKHTTP
import com.aliyun.apm.android.demo.R
import com.aliyun.apm.android.demo.databinding.ActivityNetworkAnalysisBinding
import com.aliyun.apm.android.demo.ui.DemoAcknowledgeDialogFragment

class NetworkAnalysisActivity : AppCompatActivity() {

    private var selectedLibrary = NETWORK_LIBRARY_OKHTTP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNetworkAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.topBar.topBarRoot) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(view.paddingLeft, statusBarHeight, view.paddingRight, view.paddingBottom)
            insets
        }

        binding.topBar.topBarTitle.text = "网络分析"
        binding.topBar.btnBack.setOnClickListener { finish() }

        // Dropdown setup
        val networkLibraries = listOf(NETWORK_LIBRARY_OKHTTP, NETWORK_LIBRARY_HTTP_URL_CONNECTION)
        val dropdownAdapter = NetworkLibraryDropdownAdapter(this, networkLibraries, selectedLibrary)
        binding.networkLibraryText.text = selectedLibrary

        val popupWindow = ListPopupWindow(this).apply {
            anchorView = binding.networkLibraryContainer
            setAdapter(dropdownAdapter)
            isModal = true
            setBackgroundDrawable(ContextCompat.getDrawable(this@NetworkAnalysisActivity, R.drawable.bg_network_dropdown_popup))
            verticalOffset = resources.getDimensionPixelSize(R.dimen.demo_spacing_network_dropdown_vertical_offset)
        }

        popupWindow.setOnItemClickListener { _, _, position, _ ->
            selectedLibrary = networkLibraries[position]
            binding.networkLibraryText.text = selectedLibrary
            dropdownAdapter.setSelectedItem(selectedLibrary)
            popupWindow.dismiss()
        }

        popupWindow.setOnDismissListener {
            binding.networkLibraryContainer.setBackgroundResource(R.drawable.bg_network_dropdown)
            binding.dropdownArrow.setColorFilter(
                ContextCompat.getColor(this, R.color.demo_mutable_gray)
            )
        }

        binding.networkLibraryContainer.setOnClickListener {
            binding.networkLibraryContainer.setBackgroundResource(R.drawable.bg_network_dropdown_active)
            binding.dropdownArrow.setColorFilter(
                ContextCompat.getColor(this, R.color.demo_network_field_active_border)
            )
            popupWindow.width = binding.networkLibraryContainer.width
            popupWindow.show()
        }

        binding.editUrl.hint = "请输入完整URL，默认$DEFAULT_NETWORK_REQUEST_URL"

        val onSendNetworkRequest = DemoBusinessActions.onSendNetworkRequest()
        val onTriggerNetworkError = DemoBusinessActions.onTriggerNetworkError()
        val onTriggerHttpError = DemoBusinessActions.onTriggerHttpError()

        binding.btnSendRequest.setOnClickListener {
            dismissKeyboard()
            val url = binding.editUrl.text.toString().ifBlank { DEFAULT_NETWORK_REQUEST_URL }
            onSendNetworkRequest(selectedLibrary, url)
            DemoAcknowledgeDialogFragment.show(
                supportFragmentManager,
                "发起请求",
                "已通过【$selectedLibrary】发起网络请求：$url。请切换至后台触发上报，稍后即可在 EMAS控制台 查看。"
            )
        }

        binding.btnNetworkError.setOnClickListener {
            onTriggerNetworkError()
            DemoAcknowledgeDialogFragment.show(
                supportFragmentManager,
                "网络错误",
                "已发起【网络错误】请求。请切换至后台触发上报，稍后即可在 EMAS控制台 查看。"
            )
        }

        binding.btnHttpError.setOnClickListener {
            onTriggerHttpError()
            DemoAcknowledgeDialogFragment.show(
                supportFragmentManager,
                "HTTP错误",
                "已发起【HTTP错误】请求。请切换至后台触发上报，稍后即可在 EMAS控制台 查看。"
            )
        }
    }

    private fun dismissKeyboard() {
        val imm = getSystemService(InputMethodManager::class.java)
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
        currentFocus?.clearFocus()
    }

    private class NetworkLibraryDropdownAdapter(
        private val context: Context,
        private val items: List<String>,
        private var selectedItem: String
    ) : BaseAdapter() {

        fun setSelectedItem(item: String) {
            selectedItem = item
            notifyDataSetChanged()
        }

        override fun getCount() = items.size
        override fun getItem(position: Int) = items[position]
        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.item_network_dropdown, parent, false)

            val item = items[position]
            val isSelected = item == selectedItem

            val textView = view.findViewById<TextView>(R.id.textOption)
            val indicator = view.findViewById<View>(R.id.selectedIndicator)

            textView.text = item
            textView.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isSelected) R.color.demo_network_field_active_border else R.color.demo_primary_black
                )
            )

            indicator.visibility = if (isSelected) View.VISIBLE else View.GONE

            view.setBackgroundResource(
                if (isSelected) R.drawable.bg_network_dropdown_item_selected
                else R.drawable.bg_network_dropdown_item_normal
            )

            return view
        }
    }
}
