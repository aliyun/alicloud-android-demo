package com.alibaba.httpdns.android.demo.setting

import alibaba.httpdns_android_demo.R
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class AboutUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        controller.isAppearanceLightStatusBars = true
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.httpdns_activity_about_us)
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
    }

}