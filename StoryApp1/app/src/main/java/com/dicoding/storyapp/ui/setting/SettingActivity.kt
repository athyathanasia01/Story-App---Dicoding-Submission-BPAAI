package com.dicoding.storyapp.ui.setting

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.dicoding.storyapp.databinding.ActivitySettingBinding
import com.dicoding.storyapp.preferences.ThemeViewModel
import com.dicoding.storyapp.preferences.UserPreferences
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.ui.splashandlogin.MainActivity

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    private lateinit var factory: ViewModelFactory
    private val viewModel : ThemeViewModel by viewModels { factory }

    private lateinit var preferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getFactoryInstance(application)
        preferences = UserPreferences(this)

        viewModel.getThemeSetting().observe(this) { isDarkMode ->
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchMode.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchMode.isChecked = false
            }
        }

        binding.switchMode.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            viewModel.saveThemeSetting(isChecked)
        }

        with(binding) {
            tvUsername.setText(preferences.getSession().name)
            tvUserid.setText(preferences.getSession().userId)
        }

        binding.btnToLogout.setOnClickListener { logoutApplication() }
    }

    private fun logoutApplication() {
        val intent = Intent(this@SettingActivity, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        preferences.logoutSession()
        finish()
    }
}