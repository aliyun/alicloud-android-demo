package alibaba.httpdns_android_demo.best_practice

import alibaba.httpdns_android_demo.R
import alibaba.httpdns_android_demo.SingleLiveData
import alibaba.httpdns_android_demo.TAG
import android.app.Application
import android.util.Log
import android.widget.RadioGroup
import androidx.lifecycle.viewModelScope
import com.alibaba.sdk.android.httpdns.HTTPDNSResult
import com.alibaba.sdk.android.httpdns.NetType
import com.alibaba.sdk.android.httpdns.RequestIpType
import com.alibaba.sdk.android.httpdns.net.HttpDnsNetworkDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ConnectionPool
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetAddress
import java.util.concurrent.TimeUnit

/**
 * okhttp + httpdns 最佳实践的VM
 * @author 任伟
 * @date 2024/07/23
 */
class OkHttpCaseViewModel(application: Application) : ResolveResultViewModel(application) {

    private var schemaType: SchemaType = SchemaType.HTTPS

    val path = SingleLiveData<String>().apply { value = "" }

    val showRequestAndResolveResult = SingleLiveData<Boolean>().apply { value = false }

    private lateinit var okHttpClient:OkHttpClient

    val responseStr = SingleLiveData<String>().apply { value = "" }

    var response: Response? = null
    fun initData(){
        okHttpClient = OkHttpClient.Builder()
            .connectionPool(ConnectionPool(0, 10 * 1000, TimeUnit.MICROSECONDS))
            .hostnameVerifier { _, _ ->true }
            .dns(object : Dns {
                override fun lookup(hostname: String): List<InetAddress> {
                    Log.d(TAG , "currentThread:${Thread.currentThread()}")
                    val currMills = System.currentTimeMillis()
                    //修改为最新的通俗易懂的api
                    var httpDnsResult: HTTPDNSResult? = httpDnsService?.getHttpDnsResultForHostSync(host.value , RequestIpType.both)?.apply {
                        showResolveResult(this , currMills)
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

    /**
     * 请求协议改变
     */
    fun onSchemaTypeChanged(radioGroup: RadioGroup, id: Int) {
        schemaType = when(id) {
            R.id.schema_http -> SchemaType.HTTP
            else -> SchemaType.HTTPS
        }
    }

    /**
     * 请求并解析
     */
    fun requestAndResolve(){
        if (path.value?.startsWith("/") != true) {
            path.value = "/${path.value}"
        }
        val requestUrl = "${if (schemaType == SchemaType.HTTPS) "https://" else "http://"}${host.value}${path.value}"
        Log.d(TAG , "requestUrl:$requestUrl")
        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder().url(requestUrl).build()
            okHttpClient.newCall(request).execute().use {
                val bodyStr = it.body?.string()
                Log.d(TAG , bodyStr?:"请求返回body为空")
                val code = it.code
                responseStr.postValue("$code  $bodyStr")
                showRequestAndResolveResult.postValue(true)
                response = Response(code , bodyStr)
            }
        }
    }

    /**
     * 重新输入
     */
    fun reInput(){
        showRequestAndResolveResult.value = false
    }
}