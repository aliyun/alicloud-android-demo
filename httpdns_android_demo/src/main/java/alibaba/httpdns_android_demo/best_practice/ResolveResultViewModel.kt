package alibaba.httpdns_android_demo.best_practice

import alibaba.httpdns_android_demo.HttpDnsServiceHolder
import alibaba.httpdns_android_demo.SingleLiveData
import alibaba.httpdns_android_demo.TAG
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.sdk.android.httpdns.HTTPDNSResult
import com.alibaba.sdk.android.httpdns.NetType
import com.alibaba.sdk.android.httpdns.RequestIpType
import com.alibaba.sdk.android.httpdns.net.HttpDnsNetworkDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress

/**
 * 解析的VM
 * @author 任伟
 * @date 2024/07/24
 */
open class ResolveResultViewModel(application:Application):AndroidViewModel(application) {

    /**
     * 解析服务
     */
    val httpDnsService = HttpDnsServiceHolder.getHttpDnsService(application)

    val host = SingleLiveData<String>().apply { value = "" }

    /**
     * 域名和解析耗时字符串
     */
    val hostAndTime = SingleLiveData<String>().apply { value = "" }

    /**
     * ttl时长字符串
     */
    val ttl = SingleLiveData<String>().apply { value = "" }

    /**
     * 解析的ipv4结果
     */
    val ipv4 = SingleLiveData<String>().apply { value = "" }

    /**
     * 解析的ipv6结果
     */
    val ipv6 = SingleLiveData<String>().apply { value = "" }

    /**
     * 解析结果展示
     */
    fun showResolveResult(httpDnsResult: HTTPDNSResult, mills:Long) {
        viewModelScope.launch(Dispatchers.Main) {
            hostAndTime.value = "${host.value} (${System.currentTimeMillis() - mills}ms)"
            val resultClass = httpDnsResult::class.java
            val ttlField = resultClass.getDeclaredField("ttl")
            ttlField.isAccessible = true
            val ttlValue = ttlField.getInt(httpDnsResult)
            ttl.value = "TTL: $ttlValue s"
            ipv4.value = httpDnsResult.ips.joinToString(",")
            ipv6.value = httpDnsResult.ipv6s.joinToString(",")
        }
    }

    /**
     * 同步获取解析结果
     */
    fun resolveSync(host:String , callback:(HTTPDNSResult?)->Unit){
        viewModelScope.launch(Dispatchers.Default){
            val httpDnsResultDefer = async {
                httpDnsService?.getHttpDnsResultForHostSync(host , RequestIpType.both)
            }
            withContext(Dispatchers.Main) {
                callback.invoke(httpDnsResultDefer.await())
            }

        }
    }

    /**
     * 根据网络环境,返回ip
     */
    fun processDnsResult(httpDnsResult: HTTPDNSResult, inetAddresses: MutableList<InetAddress>) {
        val ipStackType = HttpDnsNetworkDetector.getInstance().getNetType(getApplication())
        val isV6 = ipStackType == NetType.v6 || ipStackType == NetType.both
        val isV4 = ipStackType == NetType.v4 || ipStackType == NetType.both

        if (httpDnsResult.ipv6s != null && httpDnsResult.ipv6s.isNotEmpty() && isV6) {
            for (i in httpDnsResult.ipv6s.indices) {
                inetAddresses.addAll(
                    InetAddress.getAllByName(httpDnsResult.ipv6s[i]).toList()
                )
            }
        } else if (httpDnsResult.ips != null && httpDnsResult.ips.isNotEmpty() && isV4) {
            for (i in httpDnsResult.ips.indices) {
                inetAddresses.addAll(
                    InetAddress.getAllByName(httpDnsResult.ips[i]).toList()
                )
            }
        }
    }

}