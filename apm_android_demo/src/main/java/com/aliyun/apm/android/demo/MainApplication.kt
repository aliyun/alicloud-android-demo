package com.aliyun.apm.android.demo

import android.app.Application
import android.util.Log
import com.aliyun.emas.apm.Apm
import com.aliyun.emas.apm.ApmOptions
import com.aliyun.emas.apm.crash.ApmCrashAnalysisComponent
import com.aliyun.emas.apm.mem.monitor.ApmMemMonitorComponent
import com.aliyun.emas.apm.performance.ApmPerformanceComponent
import com.aliyun.emas.apm.remote.log.ApmRemoteLogComponent

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 阿里云 EMAS Apm 初始化
        val application: Application = this

        // 必填部分
        // TODO: 请将以下内容改成控制台上的信息
        val appKey = "your app key" // 请把这里改成控制台上的 "AppKey"
        val appSecret = "your app secret" // 请把这里改成控制台上的 "AppSecret"
        val appRsaSecret = "your app rsa secret" // 请把这里改成控制台上的 "AppRsaSecret"

        // 可选部分
        val channel = "EAPM demo appChannel" // App渠道
        val userId = "EAPM demo default user id" // 用户ID
        val userNick = "EAPM demo default user nick" // 用户昵称

        Apm.preStart(
            ApmOptions.Builder()
                // 必须配置application
                .setApplication(application)
                // 必须配置EMAS的appKey
                .setAppKey(appKey)
                // 必须配置EMAS的appSecret
                .setAppSecret(appSecret)
                // 使用性能分析或者远程日志，必须配置EMAS的appRsaSecret
                .setAppRsaSecret(appRsaSecret)
                // 配置使用崩溃分析功能
                .addComponent(ApmCrashAnalysisComponent::class.java)
                // 配置使用内存分析功能, 2.1.0版本新增
                .addComponent(ApmMemMonitorComponent::class.java)
                // 配置使用远程日志功能
                .addComponent(ApmRemoteLogComponent::class.java)
                // 配置使用性能分析功能
                .addComponent(ApmPerformanceComponent::class.java)
                // (可选)设置App渠道
                .setChannel(channel)
                // (可选)设置用户ID
                .setUserId(userId)
                // (可选)设置用户昵称
                .setUserNick(userNick)
                .openDebug(true)
                .build()
        )

        Apm.start()

        val preferences = getSharedPreferences(SETTINGS_PREFS, MODE_PRIVATE)
        val savedUserId = preferences.getString(KEY_USER_ID, null)
        val savedUserNick = preferences.getString(KEY_USER_NICK, null)
        savedUserId?.takeIf { it.isNotBlank() }?.let(Apm::setUserId)
        savedUserNick?.takeIf { it.isNotBlank() }?.let(Apm::setUserNick)

        Log.i(TAG, "Current APM SDK version: ${BuildConfig.APM_SDK_VERSION}")
    }

    companion object {
        private const val SETTINGS_PREFS = "demo_settings"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NICK = "user_nick"
        private const val TAG = "EAPM Demo"
    }
}
