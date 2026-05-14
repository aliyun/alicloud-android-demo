package com.aliyun.apm.android.demo.internal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aliyun.apm.android.demo.DemoBusinessActions
import com.aliyun.apm.android.demo.databinding.ActivityOtherErrorTypesBinding
import com.aliyun.apm.android.demo.ui.DemoConfirmDialogFragment
import com.aliyun.apm.android.demo.ui.customConfirmDialogConfig
import com.aliyun.apm.android.demo.ui.javaCrashConfirmDialogConfig
import com.aliyun.apm.android.demo.ui.nativeCrashConfirmDialogConfig

class OtherErrorTypesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityOtherErrorTypesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.topBar.topBarRoot) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(view.paddingLeft, statusBarHeight, view.paddingRight, view.paddingBottom)
            insets
        }

        binding.topBar.topBarTitle.text = "其他类型错误"
        binding.topBar.btnBack.setOnClickListener { finish() }

        binding.btnJavaCrashIllegalState.setOnClickListener {
            showJavaCrashConfirmDialog("Java崩溃-状态异常", DemoBusinessActions.onJavaCrashIllegalState())
        }
        binding.btnNativeCrashSigAbrt.setOnClickListener {
            showNativeCrashConfirmDialog("Native崩溃-SIGABRT", DemoBusinessActions.onNativeCrashSigAbrt())
        }
        binding.btnJavaCrashArrayIndex.setOnClickListener {
            showJavaCrashConfirmDialog("Java崩溃-数组越界", DemoBusinessActions.onJavaCrashArrayIndexOutOfBounds())
        }
        binding.btnNativeCrashSigBus.setOnClickListener {
            showNativeCrashConfirmDialog("Native崩溃-SIGBUS", DemoBusinessActions.onNativeCrashSigBus())
        }
        binding.btnJavaCrashClassCast.setOnClickListener {
            showJavaCrashConfirmDialog("Java崩溃-类型转换", DemoBusinessActions.onJavaCrashClassCast())
        }
        binding.btnNativeCrashSigIll.setOnClickListener {
            showNativeCrashConfirmDialog("Native崩溃-SIGILL", DemoBusinessActions.onNativeCrashSigIll())
        }
        binding.btnAnr.setOnClickListener {
            showCustomConfirmDialog(
                title = "ANR",
                message = "即将触发【ANR】问题，App界面会长时间无响应，系统可能弹出无响应提示，稍后即可在 EMAS控制台 看到ANR日志。",
                onConfirm = DemoBusinessActions.onAnr()
            )
        }
        binding.btnOom.setOnClickListener {
            showJavaCrashConfirmDialog("OOM", DemoBusinessActions.onJavaCrashOom())
        }
    }

    private fun showJavaCrashConfirmDialog(title: String, onConfirm: () -> Unit) {
        val config = javaCrashConfirmDialogConfig(title, onConfirm)
        DemoConfirmDialogFragment.show(
            supportFragmentManager,
            config.title,
            config.message,
            config.onConfirm
        )
    }

    private fun showNativeCrashConfirmDialog(title: String, onConfirm: () -> Unit) {
        val config = nativeCrashConfirmDialogConfig(title, onConfirm)
        DemoConfirmDialogFragment.show(
            supportFragmentManager,
            config.title,
            config.message,
            config.onConfirm
        )
    }

    private fun showCustomConfirmDialog(title: String, message: String, onConfirm: () -> Unit) {
        val config = customConfirmDialogConfig(title, message, onConfirm)
        DemoConfirmDialogFragment.show(
            supportFragmentManager,
            config.title,
            config.message,
            config.onConfirm
        )
    }
}
