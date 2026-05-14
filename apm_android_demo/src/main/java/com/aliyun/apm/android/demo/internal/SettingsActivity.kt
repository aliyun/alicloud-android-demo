package com.aliyun.apm.android.demo.internal

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aliyun.apm.android.demo.databinding.ActivitySettingsBinding
import com.aliyun.emas.apm.Apm
import com.ut.device.UTDevice

private const val SETTINGS_PREFS = "demo_settings"
private const val KEY_USER_ID = "user_id"
private const val KEY_USER_NICK = "user_nick"

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.topBar.topBarRoot) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(view.paddingLeft, statusBarHeight, view.paddingRight, view.paddingBottom)
            insets
        }

        binding.topBar.topBarTitle.text = "设置"
        binding.topBar.btnBack.setOnClickListener { finish() }

        val preferences = getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
        val utdid = UTDevice.getUtdid(this) ?: ""

        binding.editUserId.setText(preferences.getString(KEY_USER_ID, "") ?: "")
        binding.editUserNick.setText(preferences.getString(KEY_USER_NICK, "") ?: "")
        binding.textUtdid.text = utdid

        binding.btnSave.setOnClickListener {
            val imm = getSystemService(InputMethodManager::class.java)
            imm.hideSoftInputFromWindow(binding.btnSave.windowToken, 0)
            currentFocus?.clearFocus()

            val userId = binding.editUserId.text.toString()
            val userNick = binding.editUserNick.text.toString()

            preferences.edit()
                .putString(KEY_USER_ID, userId)
                .putString(KEY_USER_NICK, userNick)
                .apply()
            Apm.setUserId(userId.ifBlank { null })
            Apm.setUserNick(userNick.ifBlank { null })
        }
    }
}
