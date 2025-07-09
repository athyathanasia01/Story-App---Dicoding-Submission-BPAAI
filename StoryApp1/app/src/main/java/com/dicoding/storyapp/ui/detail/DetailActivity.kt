package com.dicoding.storyapp.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.custom.CustomDialogFragment
import com.dicoding.storyapp.databinding.ActivityDetailBinding
import com.dicoding.storyapp.network.Result
import com.dicoding.storyapp.network.response.Story
import com.dicoding.storyapp.preferences.ThemeViewModel
import com.dicoding.storyapp.preferences.UserPreferences
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.utils.changeFormatDate

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private lateinit var factory: ViewModelFactory
    private val viewModel : DetailViewModel by viewModels { factory }
    private val themeViewModel : ThemeViewModel by viewModels { factory }

    private lateinit var preferences: UserPreferences

    companion object {
        const val STORY_ID = "extra_story_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getFactoryInstance(application)

        preferences = UserPreferences(this)
        val token = preferences.getSession().token!!
        val storyId = intent?.getStringExtra(STORY_ID)!!

        Log.d("Token Get", "token : $token")

        themeViewModel.getThemeSetting().observe(this) { isDarkMode ->
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        viewModel.getDetailStory(storyId, token).observe(this) { result ->
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        showDetail(result.data.story)
                    }
                    is Result.Failed -> {
                        showLoading(false)
                        showCustomDialog(
                            "Load Story Failed",
                            result.data,
                            "Close",
                            { }
                        )
                    }
                }
            }
        }
    }

    private fun showCustomDialog(title: String, message: String, buttonText: String, action: () -> Unit) {
        CustomDialogFragment.Builder()
            .setTitle(title)
            .setMessage(message)
            .setButtonText(buttonText)
            .setOnButtonClick { action }
            .show(supportFragmentManager)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading)
            binding.progressBar.visibility = View.VISIBLE
        else
            binding.progressBar.visibility = View.GONE
    }

    private fun showDetail(detail: Story) {
        with(binding) {
            Glide.with(this@DetailActivity)
                .load(detail.photoUrl)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.error_placeholder)
                .into(storyPicture)

            tvCreator.setText(detail.name)
            tvCreatedAt.setText(detail.createdAt.changeFormatDate())
            tvDescription.setText(detail.description)
        }
    }
}