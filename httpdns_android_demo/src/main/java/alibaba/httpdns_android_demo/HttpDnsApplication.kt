package alibaba.httpdns_android_demo

import android.app.Application
import android.text.TextUtils
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
                    val timeout = preferences.getInt(KEY_TIMEOUT, 1500)
                    val region = preferences.getString(KEY_REGION, RegionText.REGION_TEXT_CHINA)
                    //自定义ttl
                    val ttlCacheStr = preferences.getString(KEY_TTL_CHANGER, null)
                    TtlCacheHolder.convertTtlCacheData(ttlCacheStr)
                    //IP探测
                    val ipRankingItemJson = preferences.getString(KEY_IP_RANKING_ITEMS, null)
                    //主站域名
                    val hostListWithFixedIpJson =
                        preferences.getString(KEY_HOST_WITH_FIXED_IP, null)
                    //预解析
                    val preResolveHostList  = preferences.getString(KEY_PRE_RESOLVE_HOST_LIST, null)

                    InitConfig.Builder()
                        .setEnableHttps(enableHttpDns)
                        .setEnableCacheIp(enableCacheIp)
                        .setEnableExpiredIp(enableExpiredIp)
                        .setRegion(textToRegion(region?:RegionText.REGION_TEXT_CHINA))
                        .setTimeout(timeout)
                        .setIPRankingList(ipRankingItemJson.toIPRankingList())
                        .configCacheTtlChanger(TtlCacheHolder.cacheTtlChanger)
                        .configHostWithFixedIp(hostListWithFixedIpJson.toHostList())
                        .buildFor(Config.ACCOUNT_ID)

                    preResolveHostList?.let {
                        val dnsService = HttpDnsServiceHolder.getHttpDnsService(this@HttpDnsApplication)
                        dnsService?.setPreResolveHosts(it.toHostList() as ArrayList<String>, RequestIpType.both)
                    }
                    HttpDnsLog.enable(preferences.getBoolean(KEY_ENABLE_LOG, false))
                }
            }
        }

//        EmasHybridApi.registerPlugin("WVPost", WVPostPlugin::class.java)

    }


}