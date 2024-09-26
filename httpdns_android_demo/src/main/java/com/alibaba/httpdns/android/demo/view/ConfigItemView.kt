package com.alibaba.httpdns.android.demo.view

import alibaba.httpdns_android_demo.R
import alibaba.httpdns_android_demo.databinding.HttpdnsLayoutToggleBinding
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.alibaba.httpdns.android.demo.toDp

class ConfigItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: HttpdnsLayoutToggleBinding? = null

    var onCheckedChangeListener: ((changed: Boolean) -> Unit)? = null

    init {
        binding = HttpdnsLayoutToggleBinding.inflate(LayoutInflater.from(context), this, true)
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ConfigItemView)
        binding?.title = ta.getString(R.styleable.ConfigItemView_title) ?: ""
        binding?.desc = ta.getString(R.styleable.ConfigItemView_desc) ?: ""
        val rightBtnIsImage = ta.getBoolean(R.styleable.ConfigItemView_rightBtnIsImage, false)
        binding?.tbToggle?.isVisible = !rightBtnIsImage
        binding?.ivRight?.isVisible = rightBtnIsImage

        ta.recycle()
        setPadding(16.toDp(), 0, 16.toDp(), 0)

        binding?.tbToggle?.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChangeListener?.invoke(isChecked)
        }
    }

    fun setTitle(title: String) {
        binding?.tvTitle?.text = title
    }

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["android:onCheckedChanged"])
        fun setOnToggleChange(view: ConfigItemView, listener: OnCheckedChangeListener) {
            view.onCheckedChangeListener = {
                listener.onCheckedChanged(it)
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["android:checked"])
        fun setChecked(view: ConfigItemView, checked: Boolean) {
            view.binding?.tbToggle?.isChecked = checked
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(changed: Boolean)
    }


}