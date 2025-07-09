package com.dicoding.storyapp.ui.liststory

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.model.repository.UserRepository

class ListStoryViewModel(private val repository: UserRepository) : ViewModel() {
    fun getListStory(token: String) = repository.getListStories(token)
}