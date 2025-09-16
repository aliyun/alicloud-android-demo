package com.alibaba.httpdns.android.demo

import alibaba.httpdns_android_demo.R
import alibaba.httpdns_android_demo.databinding.ActivityMainBinding
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * MainActivity app入口
 * @author 任伟
 * @date 2024/07/19
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var mBackKeyPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //浸入状态栏
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        controller.isAppearanceLightStatusBars = true
        //设置状态栏透明
        window.statusBarColor = Color.TRANSPARENT
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - mBackKeyPressedTime > 2000) {
            Toast.makeText(
                this@MainActivity,
                getString(R.string.toast_double_click_exit),
                Toast.LENGTH_SHORT
            ).show()
            mBackKeyPressedTime = System.currentTimeMillis()
        } else {
            finish()
        }
    }
}