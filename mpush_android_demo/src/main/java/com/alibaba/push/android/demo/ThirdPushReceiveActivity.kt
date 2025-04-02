package com.alibaba.push.android.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.sdk.android.push.popup.OnPushParseFailedListener
import com.alibaba.sdk.android.push.popup.PopupNotifyClick
import com.alibaba.sdk.android.push.popup.PopupNotifyClickListener

class ThirdPushReceiveActivity: AppCompatActivity(), PopupNotifyClickListener, OnPushParseFailedListener {

    private val mPopupNotifyClick = PopupNotifyClick(this)
    private var mTvMessage: TextView? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third_push_receive)
        mTvMessage = findViewById(R.id.tvMessage)
        mPopupNotifyClick.onCreate(this, intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        mPopupNotifyClick.onNewIntent(intent)
    }

    override fun onSysNoticeOpened(title: String?, content: String?, extraMap: MutableMap<String, String>?) {
        Log.d("ThirdPushMsg", "title:$title\ncontent:$content\nextraMap:$extraMap")
    }

    override fun onNotPushData(intent: Intent?) {
        Log.d("ThirdPushMsg", "onNotPushData")
    }

    override fun onParseFailed(intent: Intent?) {
        Log.d("ThirdPushMsg", "onParseFailed")
    }
}