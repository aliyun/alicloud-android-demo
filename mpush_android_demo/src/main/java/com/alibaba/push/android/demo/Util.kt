package com.alibaba.push.android.demo

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.StringRes
import com.alibaba.push.android.demo.databinding.InputDialogBinding
import com.alibaba.sdk.android.ams.common.logger.AmsLogger
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Int.toDp(): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()
}

fun Context.toast(@StringRes res: Int, msg: String? = null) {
    Toast.makeText(this, String.format(getString(res), msg), Toast.LENGTH_SHORT).show()
}

fun Context.showInputDialog(
    title: Int,
    hint: Int,
    showAlert: Boolean,
    showAliasInput: Boolean,
    inputCallback: ((String, String?) -> Unit)? = null
) {
    val inputDialogBinding = InputDialogBinding.inflate(LayoutInflater.from(this))
    inputDialogBinding.title = getString(title)
    inputDialogBinding.hint = getString(hint)
    inputDialogBinding.showAlert = showAlert
    inputDialogBinding.showAliasInput = showAliasInput
    val dialog = BottomSheetDialog(this, R.style.RoundedBottomSheetDialog).apply {
        setContentView(inputDialogBinding.root)
        inputDialogBinding.lifecycleOwner = this
        show()
    }
    inputDialogBinding.ivClose.setOnClickListener { dialog.dismiss() }
    inputDialogBinding.tvCancel.setOnClickListener { dialog.dismiss() }
    inputDialogBinding.tvConfirm.setOnClickListener {
        val inputText = inputDialogBinding.etInput.text.toString().trim()
        if (TextUtils.isEmpty(inputText)) {
            toast(R.string.push_input_empty)
            return@setOnClickListener
        }
        inputCallback?.invoke(inputText, inputDialogBinding.etAlias.text.toString().trim())
        dialog.dismiss()
    }
}

fun Context.getStatusBarHeight(): Int {
    val statusBarId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (statusBarId > 0) {
        resources.getDimensionPixelSize(statusBarId)
    } else {
        22.toDp()
    }
}

fun Context.getAppMetaData(key: String): String {
    try {
        val info = packageManager.getApplicationInfo(
            packageName,
            PackageManager.GET_META_DATA
        )
        if (info.metaData.containsKey(key)) {
            return "${info.metaData.get(key)}"
        }
    } catch (e: PackageManager.NameNotFoundException) {

    }
    return ""
}
