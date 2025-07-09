package com.dicoding.storyapp.ui.register

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.model.repository.UserRepository

class RegisterViewModel (private val repository: UserRepository): ViewModel() {
    fun userRegister(name: String, email: String, password: String) = repository.userRegister(name, email, password)
}