package com.alibaba.push.android.demo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.alibaba.push.android.demo.databinding.MainActivityBinding


/**
 * main activity
 * @author ren
 * @date 2024/09/20
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    private var mBackKeyPressedTime = 0L

    //处理透传消息
    private val msgReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == MESSAGE_ACTION) {
                    this@MainActivity.showMessageDialog(
                        it.getStringExtra(MESSAGE_TITLE),
                        it.getStringExtra(MESSAGE_CONTENT),
                        it.getStringExtra(MESSAGE_ID),
                        it.getStringExtra(MESSAGE_TRACE_INFO)
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //浸入状态栏
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        controller.isAppearanceLightStatusBars = true
        //设置状态栏透明
        window.statusBarColor = Color.TRANSPARENT

        binding = MainActivityBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)

        binding.viewPager.adapter = MainFragmentStateAdapter(
            this,
            mutableListOf(
                BasicFuncFragment(),
                AdvancedFuncFragment(),
                InfoFragment())
        )
        binding.viewPager.isUserInputEnabled = false
        binding.navView.itemIconTintList = null
        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_basic -> {
                    it.setChecked(true)
                    binding.viewPager.setCurrentItem(0, false)
                }
                R.id.navigation_advance -> {
                    it.setChecked(true)
                    binding.viewPager.setCurrentItem(1, false)
                }
                R.id.navigation_info -> {
                    it.setChecked(true)
                    binding.viewPager.setCurrentItem(2, false)
                }
            }
            false
        }
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

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver, IntentFilter(
            MESSAGE_ACTION))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver)
    }
}