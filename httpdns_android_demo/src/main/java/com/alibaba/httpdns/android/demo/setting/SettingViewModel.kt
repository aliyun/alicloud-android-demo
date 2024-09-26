package com.alibaba.httpdns.android.demo.setting

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import com.alibaba.httpdns.android.demo.DEFAULT_TIMEOUT
import com.alibaba.httpdns.android.demo.HttpDnsServiceHolder
import com.alibaba.httpdns.android.demo.KEY_ENABLE_AUTO_REFRESH
import com.alibaba.httpdns.android.demo.KEY_ENABLE_CACHE_IP
import com.alibaba.httpdns.android.demo.KEY_ENABLE_DEGRADE
import com.alibaba.httpdns.android.demo.KEY_ENABLE_EXPIRED_IP
import com.alibaba.httpdns.android.demo.KEY_ENABLE_HTTPS
import com.alibaba.httpdns.android.demo.KEY_ENABLE_LOG
import com.alibaba.httpdns.android.demo.KEY_PRE_RESOLVE_HOST_LIST
import com.alibaba.httpdns.android.demo.KEY_REGION
import com.alibaba.httpdns.android.demo.KEY_TIMEOUT
import com.alibaba.httpdns.android.demo.RegionText
import com.alibaba.httpdns.android.demo.SingleLiveData
import com.alibaba.httpdns.android.demo.getAccountPreference
import com.alibaba.httpdns.android.demo.textToRegion
import com.alibaba.sdk.android.httpdns.HttpDnsService
import com.alibaba.sdk.android.httpdns.log.HttpDnsLog

/**
 * 设置页面的VM
 * @author 任伟
 * @date 2024/07/19
 */
class SettingViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = getAccountPreference(getApplication())

    private var dnsService: HttpDnsService? = null

    /**
     * 是否允许过期IP
     */
    var enableExpiredIP = SingleLiveData<Boolean>().apply { value = false }

    /**
     * 是否开启本地缓存
     */
    var enableCacheIP = SingleLiveData<Boolean>().apply { value = false }

    /**
     * 是否允许HTTPS
     */
    var enableHttps = SingleLiveData<Boolean>().apply { value = false }

    /**
     * 是否开启降级
     */
    var enableDegrade = SingleLiveData<Boolean>().apply { value = false }

    /**
     * 是否允许网络切换自动刷新
     */
    var enableAutoRefresh = SingleLiveData<Boolean>().apply { value = false }

    /**
     * 是否允许打印日志
     */
    var enableLog = SingleLiveData<Boolean>().apply { value = false }

    /**
     * 当前Region
     */
    var currentRegion = SingleLiveData<String>().apply {
        value = ""
    }

    /**
     * 当前超时
     */
    var currentTimeout = SingleLiveData<String>().apply {
        value = "$DEFAULT_TIMEOUT ms"
    }

    /**
     * region弹窗展示
     */
    var regionPopupShow = SingleLiveData<Boolean>().apply {
        value = false
    }

    /**
     * 超时Dialog
     */
    var timeoutDialog: ITimeoutSettingDialog? = null

    /**
     * region设置Popup
     */
    var regionPopup: IRegionPopup? = null
    fun initData() {
        enableExpiredIP.value = preferences.getBoolean(KEY_ENABLE_EXPIRED_IP, false)
        enableCacheIP.value = preferences.getBoolean(KEY_ENABLE_CACHE_IP, false)
        enableHttps.value = preferences.getBoolean(KEY_ENABLE_HTTPS, false)
        enableDegrade.value = preferences.getBoolean(KEY_ENABLE_DEGRADE, false)
        enableAutoRefresh.value = preferences.getBoolean(KEY_ENABLE_AUTO_REFRESH, false)
        enableLog.value = preferences.getBoolean(KEY_ENABLE_LOG, false)
        currentRegion.value = preferences.getString(KEY_REGION, RegionText.REGION_TEXT_CHINA)
        currentTimeout.value = "${preferences.getInt(KEY_TIMEOUT, DEFAULT_TIMEOUT)} ms"
        dnsService = HttpDnsServiceHolder.getHttpDnsService(getApplication())
    }

    /**
     * 设置过期IP
     */
    fun toggleEnableExpiredIp(checked: Boolean) {
        enableExpiredIP.value = checked
        val editor = preferences.edit()
        editor.putBoolean(KEY_ENABLE_EXPIRED_IP, checked)
        editor.apply()
    }

    /**
     * 设置缓存IP
     */
    fun toggleEnableCacheIp(checked: Boolean) {
        enableCacheIP.value = checked
        val editor = preferences.edit()
        editor.putBoolean(KEY_ENABLE_CACHE_IP, checked)
        editor.apply()
    }

    /**
     * https
     */
    fun toggleEnableHttps(checked: Boolean) {
        enableHttps.value = checked
        val editor = preferences.edit()
        editor.putBoolean(KEY_ENABLE_HTTPS, checked)
        editor.apply()
    }

    /**
     * 是否允许降级
     */
    fun toggleEnableDegrade(checked: Boolean) {
        enableDegrade.value = checked
        val editor = preferences.edit()
        editor.putBoolean(KEY_ENABLE_DEGRADE, checked)
        editor.apply()
    }

    /**
     * 网络变化自动刷新缓存
     */
    fun toggleEnableAutoRefresh(checked: Boolean) {
        enableAutoRefresh.value = checked
        val editor = preferences.edit()
        editor.putBoolean(KEY_ENABLE_AUTO_REFRESH, checked)
        editor.apply()
    }

    /**
     * 是否允许打印log
     */
    fun toggleEnableLog(checked: Boolean) {
        enableLog.value = checked
        val editor = preferences.edit()
        editor.putBoolean(KEY_ENABLE_LOG, checked)
        editor.apply()
        HttpDnsLog.enable(checked)
    }

    /**
     * 弹出region弹窗
     */
    fun setRegion(view: View) {
        //弹窗选择region
        regionPopupShow.value = true
        regionPopup?.showRegionPopup(view)
    }

    /**
     * 设置Region
     */
    fun saveRegion(regionText: String) {
        regionPopup?.hideRegionPopup()
        currentRegion.value = regionText
        val editor = preferences.edit()
        editor.putString(KEY_REGION, regionText)
        editor.apply()
        dnsService?.setRegion(textToRegion(regionText))
    }

    /**
     * 超时设置
     */
    fun saveTimeout(timeout: Int) {
        currentTimeout.value = "$timeout ms"
        val editor = preferences.edit()
        editor.putInt(KEY_TIMEOUT, timeout)
        editor.apply()
    }

    /**
     * 弹出超时弹窗
     */
    fun showTimeoutSettingDialog() {
        timeoutDialog?.show()
    }

    /**
     * 恢复默认设置
     */
    fun reset() {
        toggleEnableExpiredIp(false)
        toggleEnableDegrade(false)
        toggleEnableCacheIp(false)
        toggleEnableHttps(false)
        toggleEnableAutoRefresh(false)
        toggleEnableLog(false)
        saveTimeout(DEFAULT_TIMEOUT)
        saveRegion(RegionText.REGION_TEXT_CHINA)
        val edit = preferences.edit()
        edit.remove(KEY_PRE_RESOLVE_HOST_LIST)
        edit.apply()
    }
}