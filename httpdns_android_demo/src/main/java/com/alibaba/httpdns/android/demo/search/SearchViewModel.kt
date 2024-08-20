package com.alibaba.httpdns.android.demo.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.alibaba.httpdns.android.demo.KEY_SEARCH_HISTORY
import com.alibaba.httpdns.android.demo.SingleLiveData
import com.alibaba.httpdns.android.demo.convertHostListToStr
import com.alibaba.httpdns.android.demo.getAccountPreference
import com.alibaba.httpdns.android.demo.readControlHostConfig
import org.json.JSONArray

/**
 * 搜索的VM
 * @author 任伟
 * @date 2024/07/19
 */
class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = getAccountPreference(getApplication())

    /**
     * 搜索历史是否可见
     */
    val inputHistoryEnableVisible = SingleLiveData<Boolean>().apply { value = false }

    /**
     * 搜索历史
     */
    val inputHistory: MutableList<String> by lazy {
        readInputHistory()
    }

    /**
     * 控制台域名列表
     */
    val controlHost: MutableList<String> by lazy {
        readControlHostConfig()
    }

    /**
     * 从 SP 中获取搜索历史
     */
    private fun readInputHistory(): MutableList<String> {
        val inputHistory = mutableListOf<String>()
        val searchHistoryJsonStr = preferences.getString(KEY_SEARCH_HISTORY, "[]")
        val jsonArrayInputHistory = JSONArray(searchHistoryJsonStr)
        if (jsonArrayInputHistory.length() != 0) {
            for (i in 0 until jsonArrayInputHistory.length()) {
                inputHistory.add(jsonArrayInputHistory.optString(i))
            }
        }
        inputHistoryEnableVisible.value = inputHistory.isNotEmpty()
        return inputHistory
    }

    /**
     * 清空搜索历史
     */
    fun clearInputHistory() {
        inputHistoryEnableVisible.value = false
        inputHistory.clear()
        val edit = preferences.edit()
        edit.putString(KEY_SEARCH_HISTORY, "[]")
        edit.apply()
    }

    /**
     * 存储搜索历史
     */
    fun saveInputHistory() {
        val edit = preferences.edit()
        edit.putString(KEY_SEARCH_HISTORY, convertHostListToStr(inputHistory))
        edit.apply()
    }

}