package com.dicoding.storyapp.ui.liststory

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.adapter.StoryListAdapter
import com.dicoding.storyapp.databinding.ActivityListStoryBinding
import com.dicoding.storyapp.network.Result
import com.dicoding.storyapp.network.response.ListStoryItem
import com.dicoding.storyapp.preferences.UserPreferences
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.ui.addstory.AddStoryActivity
import com.dicoding.storyapp.ui.detail.DetailActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.custom.CustomDialogFragment
import com.dicoding.storyapp.preferences.ThemeViewModel
import com.dicoding.storyapp.ui.setting.SettingActivity

class ListStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListStoryBinding

    private lateinit var factory: ViewModelFactory
    private val viewModel : ListStoryViewModel by viewModels { factory }
    private val themeViewModel : ThemeViewModel by viewModels { factory }

    private lateinit var preferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getFactoryInstance(application)
        preferences = UserPreferences(this)
        val token = preferences.getSession().token!!

        themeViewModel.getThemeSetting().observe(this) { isDarkMode ->
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager

        viewModel.getListStory(token).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        showListStory(result.data.listStory)
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

        binding.btnAddstory.setOnClickListener { addStoryButtonHandler() }

        supportActionBar?.elevation = 0f
        setSupportActionBar(binding.toolbar)
    }

    private fun showCustomDialog(title: String, message: String, buttonText: String, action: () -> Unit) {
        CustomDialogFragment.Builder()
            .setTitle(title)
            .setMessage(message)
            .setButtonText(buttonText)
            .setOnButtonClick { action }
            .show(supportFragmentManager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.profil -> {
                val intent = Intent(this@ListStoryActivity, SettingActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading)
            binding.progressBar.visibility = View.VISIBLE
        else
            binding.progressBar.visibility = View.GONE
    }

    private fun addStoryButtonHandler() {
        val intent = Intent(this@ListStoryActivity, AddStoryActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showListStory(storyList: List<ListStoryItem>) {
        val adapter = StoryListAdapter()
        if (storyList.isNotEmpty()) {
            adapter.submitList(storyList)
            binding.rvStories.adapter = adapter
        }

        adapter.setOnItemClickCallback(object : StoryListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                val intent = Intent(this@ListStoryActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.STORY_ID, data.id)
                startActivity(intent)
            }
        })
    }
}