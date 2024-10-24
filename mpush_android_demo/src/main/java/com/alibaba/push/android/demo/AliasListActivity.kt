package com.alibaba.push.android.demo

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.push.android.demo.databinding.AliasListBinding
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory

/**
 * 别名列表弹窗
 * @author ren
 * @date 2024-10-24
 */
class AliasListActivity: Activity() {

    private lateinit var binding: AliasListBinding
    private var aliasListAdapter: AliasListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        //设置状态栏透明
        window.statusBarColor = Color.TRANSPARENT

        binding = AliasListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvAlias.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvAlias.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
            setDrawable(ColorDrawable(ContextCompat.getColor(this@AliasListActivity, R.color.color_gray)))

        })
        aliasListAdapter = AliasListAdapter(mutableListOf()).apply {
            binding.rvAlias.adapter = this
            onItemClick = ::clickAliasCallback
        }
        binding.ivBack.setOnClickListener { finish() }
        getAlias()
    }

    private fun clickAliasCallback(alias: String) {
        setResult(RESULT_OK, Intent().apply {
            putExtra(KEY_ALIAS, alias)
        })
        finish()
    }

    private fun getAlias(){
        PushServiceFactory.getCloudPushService().listAliases(object : CommonCallback {
            override fun onSuccess(response: String?) {
                if (TextUtils.isEmpty(response)) {
                    return
                }
                val data = mutableListOf<String>()
                val aliasList = response!!.split(",")
                aliasList.reversed().forEach { alias ->
                    data.add(alias)
                }
                binding.rvAlias.post {
                    aliasListAdapter?.data?.clear()
                    aliasListAdapter?.data?.addAll(data)
                    aliasListAdapter?.notifyDataSetChanged()
                }
            }

            override fun onFailed(errorCode: String?, errorMessage: String?) {

            }

        })
    }

}