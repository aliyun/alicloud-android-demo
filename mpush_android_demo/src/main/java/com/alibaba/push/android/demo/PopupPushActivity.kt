package com.alibaba.push.android.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.alibaba.sdk.android.push.AndroidPopupActivity

class PopupPushActivity: AndroidPopupActivity() {

    private var tvMessage: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_push)
        tvMessage = findViewById(R.id.tv_message)
    }

    override fun onSysNoticeOpened(title: String?, content: String?, extraMap: MutableMap<String, String>?) {
        Log.d("PopupPushActivity", "title = $title, content = $content, extraMap = $extraMap")
    }

    override fun onNotPushData(intent: Intent?) {
        super.onNotPushData(intent)
        Log.d("PopupPushActivity", "onNotPushData")
    }

    override fun onParseFailed(intent: Intent?) {
        super.onParseFailed(intent)
        Log.d("PopupPushActivity", "onParseFailed")
    }
}