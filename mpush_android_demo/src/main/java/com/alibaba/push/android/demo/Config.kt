package com.alibaba.push.android.demo

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject


object Config {

    private const val CONFIG_FILE_NAME = "aliyun-emas-services.json"
    private const val PREFS_NAME = "push_config"
    private const val KEY_APP_KEY = "app_key"
    private const val KEY_APP_SECRET = "app_secret"

    var APP_KEY = ""
    var APP_SECRET = ""

    fun init(context: Context) {
        // 首先尝试从SharedPreferences读取用户设置的配置
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userAppKey = prefs.getString(KEY_APP_KEY, "")
        val userAppSecret = prefs.getString(KEY_APP_SECRET, "")

        if (!userAppKey.isNullOrEmpty() && !userAppSecret.isNullOrEmpty()) {
            // 如果用户设置了配置，使用用户配置
            APP_KEY = userAppKey
            APP_SECRET = userAppSecret
        } else {
            // 否则从assets文件读取默认配置
            try {
                val inputStream = context.assets.open(CONFIG_FILE_NAME)
                val buffer = ByteArray(inputStream.available())
                inputStream.read(buffer)
                inputStream.close()
                val configJsonStr = String(buffer, Charsets.UTF_8)
                val configJsonObj = JSONObject(configJsonStr).getJSONObject("config")

                if (configJsonObj.has("push.appkey")) {
                    APP_KEY = configJsonObj.optString("push.appkey")
                }

                if (configJsonObj.has("push.appsecret")) {
                    APP_SECRET = configJsonObj.optString("push.appsecret")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveUserConfig(context: Context, appKey: String, appSecret: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_APP_KEY, appKey)
            .putString(KEY_APP_SECRET, appSecret)
            .apply()
        
        // 更新当前配置
        APP_KEY = appKey
        APP_SECRET = appSecret
    }

    fun hasUserConfig(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userAppKey = prefs.getString(KEY_APP_KEY, "")
        val userAppSecret = prefs.getString(KEY_APP_SECRET, "")
        return !userAppKey.isNullOrEmpty() && !userAppSecret.isNullOrEmpty()
    }

    fun clearUserConfig(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

}