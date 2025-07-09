package com.dicoding.storyapp.network

sealed class Result<out R> private constructor() {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failed(val data: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}