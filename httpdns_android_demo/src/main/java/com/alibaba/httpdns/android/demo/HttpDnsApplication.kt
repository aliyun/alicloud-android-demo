package com.alibaba.httpdns.android.demo

import android.app.Application
import android.text.TextUtils
import com.alibaba.sdk.android.httpdns.HttpDns
import com.alibaba.sdk.android.httpdns.InitConfig
import com.alibaba.sdk.android.httpdns.RequestIpType
import com.alibaba.sdk.android.httpdns.log.HttpDnsLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author allen.wy
 * @date 2023/5/24
 */
class HttpDnsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Config.init(this)
        if (!TextUtils.isEmpty(Config.ACCOUNT_ID)) {
            CoroutineScope(Dispatchers.Default).launch {
                withContext(Dispatchers.IO) {
                    val preferences = getAccountPreference(this@HttpDnsApplication)
                    val enableExpiredIp = preferences.getBoolean(KEY_ENABLE_EXPIRED_IP, false)
                    val enableCacheIp = preferences.getBoolean(KEY_ENABLE_CACHE_IP, false)
                    val enableHttpDns = preferences.getBoolean(KEY_ENABLE_HTTPS, false)
                    val enableDegrade = preferences.getBoolean(KEY_ENABLE_DEGRADE , false)
                    val enableAutoRefresh = preferences.getBoolean(KEY_ENABLE_AUTO_REFRESH , false)
                    val timeout = preferences.getInt(
                        KEY_TIMEOUT,
                        DEFAULT_TIMEOUT
                    )
                    val region = preferences.getString(
                        KEY_REGION,
                        RegionText.REGION_TEXT_CHINA
                    )
                    //自定义ttl
                    val ttlCacheStr = preferences.getString(KEY_TTL_CHANGER, null)
                    TtlCacheHolder.convertTtlCacheData(ttlCacheStr)
                    //IP探测
                    val ipRankingItemJson = preferences.getString(KEY_IP_RANKING_ITEMS, null)
                    //主站域名
                    val hostListWithFixedIpJson =
                        preferences.getString(KEY_HOST_WITH_FIXED_IP, null)
                    //预解析
                    val preResolveHostList = preferences.getString(KEY_PRE_RESOLVE_HOST_LIST, null)

                    val initConfig = InitConfig.Builder()
                        .setEnableHttps(enableHttpDns)
                        .setEnableCacheIp(enableCacheIp)
                        .setEnableExpiredIp(enableExpiredIp)
                        .setEnableDegradationLocalDns(enableDegrade)
                        .setPreResolveAfterNetworkChanged(enableAutoRefresh)
                        .setRegion(
                            textToRegion(
                                region ?: RegionText.REGION_TEXT_CHINA
                            )
                        )
                        .setTimeoutMillis(timeout)
                        .setIPRankingList(ipRankingItemJson.toIPRankingList())
                        .configCacheTtlChanger(TtlCacheHolder.cacheTtlChanger)
                        .configHostWithFixedIp(hostListWithFixedIpJson.toHostList())
                        .build()

                    HttpDns.init(Config.ACCOUNT_ID, initConfig)

                    preResolveHostList?.let {
                        val dnsService =
                            HttpDnsServiceHolder.getHttpDnsService(this@HttpDnsApplication)
                        dnsService?.setPreResolveHosts(
                            it.toHostList() as ArrayList<String>,
                            RequestIpType.auto
                        )
                    }
                    HttpDnsLog.enable(preferences.getBoolean(KEY_ENABLE_LOG, false))
                }
            }
        }
    }
}