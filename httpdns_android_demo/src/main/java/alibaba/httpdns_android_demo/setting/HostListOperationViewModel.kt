package alibaba.httpdns_android_demo.setting

import alibaba.httpdns_android_demo.HttpDnsServiceHolder
import alibaba.httpdns_android_demo.KEY_IS_PRE_HOST
import alibaba.httpdns_android_demo.KEY_PRE_RESOLVE_HOST_LIST
import alibaba.httpdns_android_demo.SingleLiveData
import alibaba.httpdns_android_demo.convertHostListToStr
import alibaba.httpdns_android_demo.getAccountPreference
import alibaba.httpdns_android_demo.readControlHostConfig
import alibaba.httpdns_android_demo.toHostList
import android.app.Application
import android.content.Intent
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.alibaba.sdk.android.httpdns.RequestIpType
import java.util.ArrayList

/**
 * 预解析域名列表设置页面和清空指定域名缓存设置页面的VM
 * @author 任伟
 * @date 2024/07/19
 */
class HostListOperationViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val KEY_HOST_LIST = "host_list"
        const val PRE_HOST_TITLE = "预解析域名列表"
        const val CLEAR_HOST_CACHE_TITLE = "清空指定域名缓存"
        const val PRE_HOST_BTN_TEXT = "预解析域名"
        const val CLEAR_CACHE_BTN_TEXT = "清空缓存"
        const val PRE_HOST_EXPLAIN =
            "预先向HTTPDNS SDK中选择性地注册可能使用的域名，\n以便SDK提前解析，减少后续域名解析时请求的时延。"
        const val CLEAR_CACHE_EXPLAIN =
            "调用接口会清除本地缓存。下一次请求时，服务端将重新\n向权威服务器发出请求，以获取域名最新的解析IP地址。"
        const val ADD_PRE_HOST_TITLE = "添加预解析域名"
        const val ADD_CLEAR_CACHE_HOST_TITLE = "添加需要清空缓存的域名"
    }

    private val preferences = getAccountPreference(getApplication())

    /**
     * 域名列表数据
     */
    val hosts: MutableList<String> by lazy { getHostList() }

    /**
     * 选中的域名数据
     */
    val selectHosts: MutableList<String> by lazy { mutableListOf() }

    /**
     * 页面title
     */
    val title = SingleLiveData<String>().apply { value = "" }

    /**
     * 底部操作按钮文字
     */
    val btnText = SingleLiveData<String>().apply { value = "" }

    /**
     * 页面描述文字
     */
    val explainText = SingleLiveData<String>().apply { value = "" }

    /**
     * 输入弹窗的title
     */
    var addHostTitle: String = ""

    /**
     * 操作按钮是否可以点击
     */
    val btnEnableClick = MutableLiveData<Boolean>().apply { value = false }

    /**
     * 判断页面是预解析域名列表设置页面还是清空指定域名缓存设置页面
     */
    var isPreHost: Boolean = true

    /**
     * 解析服务
     */
    private val httpDnsService = HttpDnsServiceHolder.getHttpDnsService(application)

    /**
     * 获取SP缓存的域名列表
     */
    private fun getHostList(): MutableList<String> {
        var preHost: MutableList<String>?
        val hostStr = preferences.getString(KEY_HOST_LIST, null)
        if (TextUtils.isEmpty(hostStr)) {
            preHost = readControlHostConfig()
            saveHost(preHost)
        } else {
            preHost = hostStr.toHostList()
        }
        return preHost ?: mutableListOf()
    }

    /**
     * 存储域名列表
     */
    fun saveHost(hosts: MutableList<String>?) {
        if (hosts.isNullOrEmpty()) {
            return
        }
        val edit = preferences.edit()
        edit.putString(KEY_HOST_LIST, convertHostListToStr(hosts))
        edit.apply()
    }

    fun initData(intent: Intent) {
        isPreHost = intent.getBooleanExtra(KEY_IS_PRE_HOST, true)
        title.value = if (isPreHost) PRE_HOST_TITLE else CLEAR_HOST_CACHE_TITLE
        btnText.value = if (isPreHost) PRE_HOST_BTN_TEXT else CLEAR_CACHE_BTN_TEXT
        explainText.value = if (isPreHost) PRE_HOST_EXPLAIN else CLEAR_CACHE_EXPLAIN
        addHostTitle = if (isPreHost) ADD_PRE_HOST_TITLE else ADD_CLEAR_CACHE_HOST_TITLE
    }

    /**
     * 操作按钮被点击
     */
    fun operationBtnClick() {
        if (isPreHost) {
            val edit = preferences.edit()
            edit.putString(KEY_PRE_RESOLVE_HOST_LIST, convertHostListToStr(selectHosts))
            edit.apply()
            httpDnsService?.setPreResolveHosts(selectHosts, RequestIpType.both)
        } else {
            httpDnsService?.cleanHostCache(selectHosts as ArrayList<String>)
        }
    }
}