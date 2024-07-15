package alibaba.httpdns_android_demo

import android.content.Context
import org.json.JSONObject


object Config {

    const val CONFIG_FILE_NAME = "aliyun-emas-services.json"

    var ACCOUNT_ID = ""

    var SECRET_KEY = ""

    fun init(context: Context){
        val inputStream = context.assets.open(CONFIG_FILE_NAME)
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()
        val configJsonStr = String(buffer , Charsets.UTF_8)
        val configJsonObj = JSONObject(configJsonStr)

        if (configJsonObj.has("httpdns.accountId")) {
            ACCOUNT_ID = configJsonObj.optString("httpdns.accountId")
        }

        if (configJsonObj.has("httpdns.secretKey")){
            SECRET_KEY = configJsonObj.optString("httpdns.secretKey")
        }


    }

}