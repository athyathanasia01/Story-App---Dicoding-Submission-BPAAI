package com.dicoding.storyapp.ui.splashandlogin

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.model.repository.UserRepository

class LoginViewModel (private val repository: UserRepository) : ViewModel() {
    fun saveSessionLogin(email: String, password: String) = repository.userLogin(email, password)
}