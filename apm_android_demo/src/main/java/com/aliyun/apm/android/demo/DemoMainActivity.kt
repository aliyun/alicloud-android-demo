package com.aliyun.apm.android.demo

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.ViewGroup
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.aliyun.apm.android.demo.databinding.ActivityDemoMainBinding
import com.aliyun.apm.android.demo.internal.Lag
import com.aliyun.apm.android.demo.internal.LargeObject
import com.aliyun.apm.android.demo.internal.LeakActivity
import com.aliyun.apm.android.demo.internal.NetworkAnalysisActivity
import com.aliyun.apm.android.demo.internal.OtherErrorTypesActivity
import com.aliyun.apm.android.demo.internal.PageAnalysisActivity
import com.aliyun.apm.android.demo.internal.SettingsActivity
import com.aliyun.apm.android.demo.ui.AdvancedAcknowledgeSection
import com.aliyun.apm.android.demo.ui.DemoAcknowledgeDialogFragment
import com.aliyun.apm.android.demo.ui.DemoAdvancedAcknowledgeDialogFragment
import com.aliyun.apm.android.demo.ui.DemoConfirmDialogFragment
import com.aliyun.apm.android.demo.ui.MemoryIssueTriggerResult
import com.aliyun.apm.android.demo.ui.PositiveUploadSheetFragment
import com.aliyun.apm.android.demo.ui.RemoteLogRecallSheetFragment
import com.aliyun.apm.android.demo.ui.customConfirmDialogConfig
import com.aliyun.apm.android.demo.ui.javaCrashConfirmDialogConfig
import com.aliyun.apm.android.demo.ui.nativeCrashConfirmDialogConfig
import com.aliyun.emas.apm.crash.ApmCrashAnalysis
import com.aliyun.emas.apm.mem.monitor.ApmMemMonitor
import com.aliyun.emas.apm.remote.log.ApmRemoteLog
import okhttp3.OkHttpClient
import okhttp3.Request

class DemoMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val binding = ActivityDemoMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = true

        val settingsTopSpacing = resources.getDimensionPixelSize(R.dimen.demo_spacing_header_settings_top)
        ViewCompat.setOnApplyWindowInsetsListener(binding.btnSettings) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = statusBarHeight + settingsTopSpacing
            }
            insets
        }
        ViewCompat.requestApplyInsets(binding.root)

        binding.btnSettings.setOnClickListener {
            DemoBusinessActions.onOpenSettings(this)()
        }

        setupNoticeBanner(binding)
        setupCrashAnalysisSection(binding)
        setupPerformanceSection(binding)
        setupMemorySection(binding)
        setupRemoteLogSection(binding)
    }

    private fun setupNoticeBanner(binding: ActivityDemoMainBinding) {
        val fullText = "触发相关事件，并在 EMAS控制台 查看上报数据"
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf("EMAS控制台")
        val end = start + "EMAS控制台".length
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#2A313D")),
            start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.noticeBanner.text = spannable
    }

    private fun setupCrashAnalysisSection(binding: ActivityDemoMainBinding) {
        binding.btnJavaCrashNpe.setOnClickListener {
            showJavaCrashConfirmDialog("Java崩溃-空指针", DemoBusinessActions.onJavaCrashNpe())
        }
        binding.btnNativeCrashSigSegv.setOnClickListener {
            showNativeCrashConfirmDialog("Native崩溃-SIGSEGV", DemoBusinessActions.onNativeCrashSigSegv())
        }
        binding.btnLag.setOnClickListener {
            showCustomConfirmDialog(
                title = "卡顿",
                message = "即将触发【卡顿】问题，App界面会短暂卡住，稍后即可在 EMAS控制台 看到卡顿日志。",
                onConfirm = DemoBusinessActions.onShiftingStackLag()
            )
        }
        binding.btnCustomException.setOnClickListener {
            DemoBusinessActions.onCustomException()()
            DemoAcknowledgeDialogFragment.show(
                supportFragmentManager,
                "自定义异常",
                "已触发多条自定义异常，请在 EMAS控制台 查看自定义异常详情。"
            )
        }
        binding.btnOtherErrorTypes.setOnClickListener {
            DemoBusinessActions.onOpenOtherErrorTypes(this)()
        }
    }

    private fun setupPerformanceSection(binding: ActivityDemoMainBinding) {
        binding.btnLaunchAnalysis.setOnClickListener {
            DemoAdvancedAcknowledgeDialogFragment.show(
                supportFragmentManager,
                "启动分析",
                listOf(
                    AdvancedAcknowledgeSection("冷启动", "已在App启动时自动记录"),
                    AdvancedAcknowledgeSection("热启动", "需将App进行前后台切换，产生日志"),
                    AdvancedAcknowledgeSection("查看数据", "所有启动数据均在App退至后台时统一上报，之后即可在 EMAS控制台 查看")
                )
            )
        }
        binding.btnPageAnalysis.setOnClickListener {
            DemoBusinessActions.onOpenPageAnalysis(this)()
        }
        binding.btnNetworkAnalysis.setOnClickListener {
            DemoBusinessActions.onOpenNetworkAnalysis(this)()
        }
    }

    private fun setupMemorySection(binding: ActivityDemoMainBinding) {
        binding.btnOom.setOnClickListener {
            showJavaCrashConfirmDialog("OOM", DemoBusinessActions.onJavaCrashOom())
        }
        binding.btnMemoryIssues.setOnClickListener {
            when (DemoBusinessActions.onTriggerMemoryIssues(this)()) {
                MemoryIssueTriggerResult.Triggered -> {
                    DemoAcknowledgeDialogFragment.show(
                        supportFragmentManager,
                        "内存泄漏/大对象",
                        "已触发【内存泄漏/大对象】等内存问题，请在 EMAS控制台 查看。"
                    )
                }
                MemoryIssueTriggerResult.AlreadyTriggered -> {
                    DemoAcknowledgeDialogFragment.show(
                        supportFragmentManager,
                        "内存泄漏/大对象",
                        "App生命周期内，最多仅会触发一次【内存泄漏/大对象】内存检测。请退出重启再触发。"
                    )
                }
            }
        }
    }

    private fun setupRemoteLogSection(binding: ActivityDemoMainBinding) {
        binding.btnRemoteLogPull.setOnClickListener {
            DemoBusinessActions.onPrintRemoteLog()()
            RemoteLogRecallSheetFragment.show(supportFragmentManager)
        }
        binding.btnPositiveUpload.setOnClickListener {
            DemoBusinessActions.onPositiveUpload()()
            PositiveUploadSheetFragment.show(supportFragmentManager)
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

internal object DemoBusinessActions {
    private const val SETTINGS_CLICK_DEBOUNCE_MS = 600L
    private const val PREFIX = "EAPM demo: "
    private const val ACTION_LOG_TAG = "EAPM Demo"
    internal const val NETWORK_LIBRARY_OKHTTP = "OkHttp"
    internal const val NETWORK_LIBRARY_HTTP_URL_CONNECTION = "HttpUrlConnection"
    internal const val DEFAULT_NETWORK_REQUEST_URL = "https://www.aliyun.com"

    private val nativeLib by lazy { NativeLib() }
    private val okHttpClient by lazy { OkHttpClient() }
    private var largeObject: LargeObject? = null
    private var lastSettingsLaunchAt = 0L
    private var hasTriggeredMemoryIssuesInProcess = false

    fun onJavaCrashNpe(): () -> Unit = {
        logAction("Start trigger Java crash NPE")
        Thread(
            { throw NullPointerException(PREFIX + "Java Null Pointer Exception") },
            "JavaCrashNpeThread"
        ).start()
    }

    fun onNativeCrashSigSegv(): () -> Unit = {
        logAction("Start trigger native crash SIGSEGV")
        nativeLib.mockSigSegv()
    }

    fun onShiftingStackLag(): () -> Unit = {
        logAction("Start trigger lag")
        Handler(Looper.getMainLooper()).post {
            Lag().blockByShiftingStack()
        }
    }

    fun onAnr(): () -> Unit = {
        logAction("Start trigger ANR")
        SystemClock.sleep(12000)
    }

    fun onCustomException(): () -> Unit = {
        logAction("Start trigger custom exception")
        repeat(8) { index ->
            ApmCrashAnalysis.getInstance()
                .recordException(
                    RuntimeException(PREFIX + "Custom Exception [${index + 1}/8]")
                )
        }
    }

    fun onOpenOtherErrorTypes(activity: Activity): () -> Unit = {
        logAction("Start open other error types page")
        activity.startActivity(Intent(activity, OtherErrorTypesActivity::class.java))
    }

    fun onOpenSettings(activity: Activity): () -> Unit = {
        logAction("Start open settings page")
        val now = SystemClock.elapsedRealtime()
        if (now - lastSettingsLaunchAt >= SETTINGS_CLICK_DEBOUNCE_MS) {
            lastSettingsLaunchAt = now
            activity.startActivity(Intent(activity, SettingsActivity::class.java))
        }
    }

    fun onOpenPageAnalysis(activity: Activity): () -> Unit = {
        logAction("Start open page analysis page")
        activity.startActivity(Intent(activity, PageAnalysisActivity::class.java))
    }

    fun onOpenNetworkAnalysis(activity: Activity): () -> Unit = {
        logAction("Start open network analysis page")
        activity.startActivity(Intent(activity, NetworkAnalysisActivity::class.java))
    }

    fun onSendNetworkRequest(): (String, String) -> Unit = { library, url ->
        logAction("Start send network request by $library to $url")
        Thread(
            {
                try {
                    when (library) {
                        NETWORK_LIBRARY_OKHTTP -> sendOkHttpRequest(url)
                        else -> sendHttpUrlConnectionRequest(url)
                    }
                } catch (_: IOException) {
                    // Network request failures in the demo should not crash the app.
                }
            },
            "NetworkRequestThread"
        ).start()
    }

    fun onTriggerNetworkError(): () -> Unit = {
        logAction("Start trigger network error")
        Thread(
            {
                try {
                    openConnection("https://eapm-demo.invalid/")
                        .applyDemoBusinessHeaders("network-error")
                        .useForRequest { connection ->
                        connection.responseCode
                    }
                } catch (_: IOException) {
                    // Keep the failure on the request path but don't let the demo crash.
                }
            },
            "NetworkErrorThread"
        ).start()
    }

    fun onTriggerHttpError(): () -> Unit = {
        logAction("Start trigger HTTP error")
        Thread(
            {
                try {
                    openConnection("https://httpbin.org/status/500")
                        .applyDemoBusinessHeaders("http-error")
                        .useForRequest { connection ->
                        connection.responseCode
                        connection.errorStream?.close()
                    }
                } catch (_: IOException) {
                    // A transport failure here should not crash the demo app.
                }
            },
            "HttpErrorThread"
        ).start()
    }

    fun onJavaCrashOom(): () -> Unit = {
        logAction("Start trigger Java crash OOM")
        Thread(
            { throw OutOfMemoryError(PREFIX + "OOM") },
            "JavaCrashOomThread"
        ).start()
    }

    fun onTriggerMemoryIssues(activity: Activity): () -> MemoryIssueTriggerResult = {
        if (hasTriggeredMemoryIssuesInProcess) {
            MemoryIssueTriggerResult.AlreadyTriggered
        } else {
            logAction("Start trigger memory issues")
            hasTriggeredMemoryIssuesInProcess = true
            activity.startActivity(Intent(activity, LeakActivity::class.java))
            largeObject = LargeObject()
            ApmMemMonitor.getInstance().triggerMemAnalysis()
            MemoryIssueTriggerResult.Triggered
        }
    }

    fun onPrintRemoteLog(): () -> String = {
        logAction("Start print remote log")
        ApmRemoteLog.i(
            PREFIX + "module",
            PREFIX + "tag",
            "日志回捞：This is an info level log from EAPM Demo"
        )
        "打印日志成功"
    }

    fun onPositiveUpload(): () -> String = {
        logAction("Start positive upload")
        ApmRemoteLog.i(
            PREFIX + "module",
            PREFIX + "tag",
            "主动上报：This is an info level log from EAPM Demo"
        )
        ApmRemoteLog.positiveUploadLog(PREFIX + "bizComment备注, 用于在控制台上查找日志")
        "主动上报成功"
    }

    fun onJavaCrashIllegalState(): () -> Unit = {
        logAction("Start trigger Java crash IllegalState")
        Thread(
            { throw IllegalStateException(PREFIX + "Java Illegal State Exception") },
            "JavaCrashIllegalStateThread"
        ).start()
    }

    fun onNativeCrashSigAbrt(): () -> Unit = {
        logAction("Start trigger native crash SIGABRT")
        nativeLib.mockSigAbrt()
    }

    fun onJavaCrashArrayIndexOutOfBounds(): () -> Unit = {
        logAction("Start trigger Java crash ArrayIndexOutOfBounds")
        Thread(
            {
                throw ArrayIndexOutOfBoundsException(
                    PREFIX + "Java Array Index Out Of Bounds Exception"
                )
            },
            "JavaCrashArrayIndexOutOfBoundsThread"
        ).start()
    }

    fun onNativeCrashSigBus(): () -> Unit = {
        logAction("Start trigger native crash SIGBUS")
        nativeLib.mockSigBus()
    }

    fun onJavaCrashClassCast(): () -> Unit = {
        logAction("Start trigger Java crash ClassCast")
        Thread(
            { throw ClassCastException(PREFIX + "Java Class Cast Exception") },
            "JavaCrashClassCastThread"
        ).start()
    }

    fun onNativeCrashSigIll(): () -> Unit = {
        logAction("Start trigger native crash SIGILL")
        nativeLib.mockSigIll()
    }

    private fun openConnection(url: String): HttpURLConnection {
        return (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 5_000
            readTimeout = 5_000
            instanceFollowRedirects = true
            useCaches = false
        }
    }

    private fun HttpURLConnection.applyDemoBusinessHeaders(scene: String): HttpURLConnection {
        setRequestProperty("x-eapm-demo-scene", scene)
        setRequestProperty("x-eapm-demo-source", "android-demo")
        return this
    }

    private fun sendOkHttpRequest(url: String) {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        okHttpClient.newCall(request).execute().use { }
    }

    private fun sendHttpUrlConnectionRequest(url: String) {
        openConnection(url).useForRequest { connection ->
            connection.responseCode
            connection.inputStream?.close()
            connection.errorStream?.close()
        }
    }

    private inline fun HttpURLConnection.useForRequest(block: (HttpURLConnection) -> Unit) {
        try {
            connect()
            block(this)
        } finally {
            disconnect()
        }
    }

    private fun logAction(message: String) {
        Log.i(ACTION_LOG_TAG, message)
    }
}
