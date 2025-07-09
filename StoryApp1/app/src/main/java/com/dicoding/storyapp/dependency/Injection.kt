package com.dicoding.storyapp.dependency

import android.app.Application
import com.dicoding.storyapp.model.repository.UserRepository
import com.dicoding.storyapp.network.api.ApiConfig
import com.dicoding.storyapp.network.api.ApiService
import com.dicoding.storyapp.preferences.ThemePreferences
import com.dicoding.storyapp.preferences.dataStore

object Injection {
    fun provideRepository() : UserRepository {
        val apiService: ApiService = ApiConfig.getApiService()

        return UserRepository.getRespositoryInstance(apiService)
    }

    fun provideTheme(application: Application) : ThemePreferences {
        return ThemePreferences.getThemeInstance(application.dataStore)
    }
}