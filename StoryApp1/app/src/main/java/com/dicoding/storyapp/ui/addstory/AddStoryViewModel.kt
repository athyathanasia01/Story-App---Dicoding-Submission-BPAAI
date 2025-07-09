package com.dicoding.storyapp.ui.addstory

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.model.repository.UserRepository
import java.io.File

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {
    fun addNewStory(token: String, description: String, image: File) = repository.addNewStory(token, description, image)
}