package com.alibaba.push.android.demo

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat

class AliasListActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        //设置状态栏透明
        window.statusBarColor = Color.TRANSPARENT
    }

}