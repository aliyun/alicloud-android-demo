package alibaba.httpdns_android_demo.best_practice

import alibaba.httpdns_android_demo.TAG
import android.app.Application
import android.util.Log
import com.alibaba.sdk.android.httpdns.HTTPDNSResult
import com.alibaba.sdk.android.httpdns.RequestIpType
import okhttp3.ConnectionPool
import okhttp3.Dns
import okhttp3.OkHttpClient
import java.net.InetAddress
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * ExoPlayer最佳实践的VM
 * @author 任伟
 * @date 2024/07/25
 */
class ExoPlayerCaseViewModel(application: Application) : ResolveResultViewModel(application) {

    val playerUrl = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd"

    lateinit var okHttpClient: OkHttpClient

    fun initData() {
        host.value = URL(playerUrl).host

        okHttpClient = OkHttpClient.Builder()
            .connectionPool(ConnectionPool(0, 10 * 1000, TimeUnit.MICROSECONDS))
            .hostnameVerifier { _, _ -> true }
            .dns(object : Dns {
                override fun lookup(hostname: String): List<InetAddress> {
                    Log.d(TAG, "currentThread:${Thread.currentThread()}")
                    var httpDnsResult: HTTPDNSResult? = null
                    val currMills = System.currentTimeMillis()
                    //修改为最新的通俗易懂的api
                    resolveSync(host.value!!) {
                        it?.apply {
                            showResolveResult(this, currMills)
                            httpDnsResult = this
                        }
                    }
                    val inetAddresses = mutableListOf<InetAddress>()
                    Log.d(TAG, "httpdns $hostname 解析结果 $httpDnsResult")
                    httpDnsResult?.let { processDnsResult(it, inetAddresses) }

                    if (inetAddresses.isEmpty()) {
                        Log.d(TAG, "httpdns 未返回IP，走local dns")
                        return Dns.SYSTEM.lookup(hostname)
                    }
                    return inetAddresses
                }
            })
            .build()
    }


}