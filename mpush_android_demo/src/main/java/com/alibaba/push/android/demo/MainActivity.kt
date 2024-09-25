package com.alibaba.push.android.demo

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.alibaba.push.android.demo.databinding.MainActivityBinding

/**
 * main activity
 * @author ren
 * @date 2024/09/20
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private lateinit var viewModel: MainViewModel

    private var mBackKeyPressedTime = 0L

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
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.viewModel = viewModel
        binding.viewPager.adapter = MainFragmentStateAdapter(
            this,
            mutableListOf(BasicFuncFragment(), AdvancedFuncFragment(), InfoFragment())
        )

        binding.viewPager.registerOnPageChangeCallback(object:OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                viewModel.fragmentIndex.value = position
            }
        })

        viewModel.tabClickCallBack = {
            binding.viewPager.currentItem = it
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

}