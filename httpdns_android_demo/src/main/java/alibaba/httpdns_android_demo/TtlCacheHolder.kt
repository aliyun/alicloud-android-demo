package alibaba.httpdns_android_demo

import com.alibaba.sdk.android.httpdns.CacheTtlChanger
import org.json.JSONException
import org.json.JSONObject

/**
 * @author allen.wy
 * @date 2023/6/6
 */
object TtlCacheHolder {
    var ttlCache = mutableMapOf<String, Int>()

    val cacheTtlChanger = CacheTtlChanger { host, _, ttl ->
        if (ttlCache[host] != null) ttlCache[host]!! else ttl
    }

    fun convertTtlCacheData(cacheData: String?) {
        if (cacheData == null) {
            return
        }
        try {
            val jsonObject = JSONObject(cacheData)
            val it = jsonObject.keys()
            while (it.hasNext()) {
                val host = it.next()
                ttlCache[host] = jsonObject.getInt(host)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    fun MutableMap<String, Int>?.toJsonString(): String? {
        if (this == null) {
            return null
        }
        val jsonObject = JSONObject()
        for (host in this.keys) {
            try {
                jsonObject.put(host, this[host])
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return jsonObject.toString()
    }

}