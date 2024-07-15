package alibaba.httpdns_android_demo

import android.content.Context
import android.text.TextUtils
import com.alibaba.sdk.android.httpdns.HttpDns
import com.alibaba.sdk.android.httpdns.HttpDnsService

/**
 * @author allen.wy
 * @date 2023/6/6
 */
object HttpDnsServiceHolder {

    fun getHttpDnsService(context: Context) : HttpDnsService? {
        val dnsService = if (!TextUtils.isEmpty(Config.ACCOUNT_ID)) {
            if (!TextUtils.isEmpty(Config.SECRET_KEY)) HttpDns.getService(
                context,
                Config.ACCOUNT_ID, Config.SECRET_KEY
            ) else HttpDns.getService(
                context,
                Config.ACCOUNT_ID
            )
        } else null

        return dnsService
    }

}