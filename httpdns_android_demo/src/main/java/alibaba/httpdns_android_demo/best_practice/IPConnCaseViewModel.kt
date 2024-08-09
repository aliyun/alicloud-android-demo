package alibaba.httpdns_android_demo.best_practice

import alibaba.httpdns_android_demo.SingleLiveData
import alibaba.httpdns_android_demo.TAG
import alibaba.httpdns_android_demo.readStringFrom
import android.app.Application
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.alibaba.sdk.android.httpdns.RequestIpType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection

/**
 * IP直连的VM
 * @author 任伟
 * @date 2024/07/24
 */
class IPConnCaseViewModel(application: Application) : ResolveResultViewModel(application) {

    val responseStr = SingleLiveData<String>().apply { value = "" }

    fun initData() {
        host.value = "suggest.taobao.com"
        sniRequest()
    }

    fun sniRequest() {
        val testUrl = "https://suggest.taobao.com/sug?code=utf-8&q=phone"
        viewModelScope.launch(Dispatchers.IO) {
            recursiveRequest(testUrl) { message ->
                responseStr.postValue(message)
            }
        }
    }

    private suspend fun recursiveRequest(url: String, callback: suspend (message: String) -> Unit) {
        val host = URL(url).host
        var ipURL: String? = null

        val currMill = System.currentTimeMillis()
        //解析
        val result = httpDnsService?.getHttpDnsResultForHostSync(host , RequestIpType.both)?.apply {
            Log.d(TAG, this.toString())
            showResolveResult(this, currMill)
        }

        val hostIP = getIp(result)
        if (TextUtils.isEmpty(hostIP)) {
            return
        }

        ipURL = url.replace(host, hostIP!!)
        val conn: HttpsURLConnection =
            URL(ipURL).openConnection() as HttpsURLConnection
        conn.setRequestProperty("Host", host)
        conn.connectTimeout = 30000
        conn.readTimeout = 30000
        conn.instanceFollowRedirects = false

        //设置SNI
        val sslSocketFactory = TLSSNISocketFactory(conn)
        // SNI场景，创建SSLSocket
        conn.sslSocketFactory = sslSocketFactory
        conn.hostnameVerifier = HostnameVerifier { _, session ->
            val requestHost = conn.getRequestProperty("Host") ?: conn.url.host
            HttpsURLConnection.getDefaultHostnameVerifier().verify(requestHost, session)
        }
        val code = conn.responseCode
        if (needRedirect(code)) {
            //临时重定向和永久重定向location的大小写有区分
            var location = conn.getHeaderField("Location")
            if (location == null) {
                location = conn.getHeaderField("location")
            }
            if (!(location!!.startsWith("http://") || location.startsWith("https://"))) {
                //某些时候会省略host，只返回后面的path，所以需要补全url
                val originalUrl = URL(url)
                location = (originalUrl.protocol + "://"
                        + originalUrl.host + location)
            }
            recursiveRequest(location, callback)
        } else {
            val inputStream: InputStream?
            val streamReader: BufferedReader?
            if (code != HttpURLConnection.HTTP_OK) {
                inputStream = conn.errorStream
                var errMsg: String? = null
                if (inputStream != null) {
                    streamReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
                    errMsg = readStringFrom(streamReader).toString()
                }
                Log.d("httpdns", "SNI request error: $errMsg")
                callback("$code - $errMsg")
            } else {
                inputStream = conn.inputStream
                streamReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
                val body: String = readStringFrom(streamReader).toString()
                Log.d("httpdns", "SNI request response: $body")
                callback("$code - $body")
            }
        }
    }

    private fun needRedirect(code: Int): Boolean {
        return code in 300..399
    }

}