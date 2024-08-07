package alibaba.httpdns_android_demo.function

import alibaba.httpdns_android_demo.HttpDnsServiceHolder
import alibaba.httpdns_android_demo.KEY_HOST
import alibaba.httpdns_android_demo.KEY_RESOLVE_API_TYPE
import alibaba.httpdns_android_demo.SingleLiveData
import alibaba.httpdns_android_demo.best_practice.Response
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import com.alibaba.sdk.android.httpdns.HTTPDNSResult
import com.alibaba.sdk.android.httpdns.RequestIpType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

/**
 * 解析欢迎页和解析结果页的VM
 * @author 任伟
 * @date 2024/07/19
 */
class ResolveViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val RESOLVE_SYNC_DESC =
            "使用同步解析接口解析域名，会阻塞当前线程，直到获得有效解析结果并返回"
        const val RESOLVE_ASYNC_DESC =
            "使用异步解析接口解析域名，不会阻塞当前线程，解析结果会通过回调的形式返回。"
        const val RESOLVE_SYNC_NONBLOCKING_DESC =
            "使用同步非阻塞接口解析域名，不会阻塞当前线程，但可能会返回空结果。"
    }

    /**
     * 域名
     */
    var host = SingleLiveData<String>().apply { value = "" }

    /**
     * 当前解析的api接口类型
     */
    var currResolveApiType = SingleLiveData<Int>().apply { value = ResolveApiType.RESOLVE_SYNC }

    /**
     * 当前解析api接口类型的描述
     */
    var resolveApiTypeDesc = SingleLiveData<String>().apply { value = RESOLVE_SYNC_DESC }

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

    val showResolveFailHint = SingleLiveData<Boolean>().apply { value = false }

    /**
     * 解析服务
     */
    private val httpDnsService = HttpDnsServiceHolder.getHttpDnsService(application)

    fun initData(bundle: Bundle?) {
        host.value = bundle?.getString(KEY_HOST)
        currResolveApiType.value = bundle?.getInt(KEY_RESOLVE_API_TYPE)
        resolve()
    }

    /**
     * 点击解析接口选择按钮
     */
    fun clickResolveTypeBtn(type: Int) {
        currResolveApiType.value = type
        when (type) {
            ResolveApiType.RESOLVE_SYNC -> {
                resolveApiTypeDesc.value = RESOLVE_SYNC_DESC
            }

            ResolveApiType.RESOLVE_ASYNC -> {
                resolveApiTypeDesc.value = RESOLVE_ASYNC_DESC
            }

            else -> {
                resolveApiTypeDesc.value = RESOLVE_SYNC_NONBLOCKING_DESC
            }
        }
    }

    /**
     * 解析
     */
    fun resolve() {
        when (currResolveApiType.value) {
            ResolveApiType.RESOLVE_SYNC -> {
                resolveSync()
            }

            ResolveApiType.RESOLVE_ASYNC -> {
                resolveAsync()
            }

            else -> {
                resolveSyncNonBlocking()
            }
        }
    }

    /**
     * 同步非阻塞解析
     */
    private fun resolveSyncNonBlocking() {
        val currMills = System.currentTimeMillis()
        httpDnsService?.getHttpDnsResultForHostSyncNonBlocking(host.value, RequestIpType.both)?.apply {
                showResolveResult(this, currMills)
            }
    }

    /**
     * 异步解析
     */
    private fun resolveAsync() {
        val currMills = System.currentTimeMillis()
        httpDnsService?.getHttpDnsResultForHostAsync(host.value, RequestIpType.both) {
            CoroutineScope(Dispatchers.Main).launch {
                showResolveResult(it, currMills)
            }
        }
    }

    /**
     * 同步解析
     */
    private fun resolveSync() {
        val currMills = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Default).launch {
            val result = async {
                httpDnsService?.getHttpDnsResultForHostSync(host.value, RequestIpType.both)
            }.await()
            withContext(Dispatchers.Main) {
                result?.apply {
                    showResolveResult(this, currMills)
                }
            }
        }
    }

    /**
     * 解析结果展示
     */
    private fun showResolveResult(httpDnsResult: HTTPDNSResult, mills: Long) {
        if (httpDnsResult.ips.isEmpty() && httpDnsResult.ipv6s.isEmpty()) {
            //未获取解析结果
            hostAndTime.value = ""
            ttl.value = ""
            ipv4.value = ""
            ipv6.value = ""
            showResolveFailHint.value = true
        }else {
            hostAndTime.value = "${host.value} (${System.currentTimeMillis() - mills}ms)"
            val resultClass = httpDnsResult::class.java
            val ttlField = resultClass.getDeclaredField("ttl")
            ttlField.isAccessible = true
            val ttlValue = ttlField.getInt(httpDnsResult)
            ttl.value = "TTL: $ttlValue s"
            ipv4.value = httpDnsResult.ips.joinToString(",")
            ipv6.value = httpDnsResult.ipv6s.joinToString(",")
            showResolveFailHint.value = false
        }

    }

    /**
     * 域名预加载
     */
    fun preHost() {
        httpDnsService?.setPreResolveHosts(arrayListOf(host.value), RequestIpType.both)
    }

    /**
     * 清空当前域名缓存
     */
    fun clearHostCache() {
        httpDnsService?.cleanHostCache(ArrayList<String>().apply { add(host.value ?: "") })
        hostAndTime.value = ""
        ttl.value = ""
        ipv4.value = ""
        ipv6.value = ""
    }
}

object ResolveApiType {
    /**
     * 同步
     */
    const val RESOLVE_SYNC = 0

    /**
     * 异步
     */
    const val RESOLVE_ASYNC = 1

    /**
     * 同步非阻塞
     */
    const val RESOLVE_SYNC_NONBLOCKING = 2
}