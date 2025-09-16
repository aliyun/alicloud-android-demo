package com.alibaba.httpdns.android.demo

import android.content.Context
import org.json.JSONObject


object Config {

    private const val CONFIG_FILE_NAME = "aliyun-emas-services.json"

    private const val CONTROL_HOST_CONFIG_FILE = "httpdns-domains.json"

    var ACCOUNT_ID = ""

    var SECRET_KEY = ""

    var CONTROL_HOST_JSON = ""

    fun init(context: Context) {
        val inputStream = context.assets.open(CONFIG_FILE_NAME)
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()
        val configJsonStr = String(buffer, Charsets.UTF_8)
        val configJsonObj = JSONObject(configJsonStr).getJSONObject("config")


        if (configJsonObj.has("httpdns.accountId")) {
            ACCOUNT_ID = configJsonObj.optString("httpdns.accountId")
        }

        if (configJsonObj.has("httpdns.secretKey")) {
            SECRET_KEY = configJsonObj.optString("httpdns.secretKey")
        }

        val controlHostStream = context.assets.open(CONTROL_HOST_CONFIG_FILE)
        val controlHostBuffer = ByteArray(controlHostStream.available())
        controlHostStream.read(controlHostBuffer)
        controlHostStream.close()
        CONTROL_HOST_JSON = String(controlHostBuffer, Charsets.UTF_8)
    }

}