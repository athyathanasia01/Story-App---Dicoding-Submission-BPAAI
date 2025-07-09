package com.dicoding.storyapp.ui.detail

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.model.repository.UserRepository

class DetailViewModel(private val repository: UserRepository) : ViewModel() {
    fun getDetailStory(id : String, token: String) = repository.DetailStory(id, token)
}