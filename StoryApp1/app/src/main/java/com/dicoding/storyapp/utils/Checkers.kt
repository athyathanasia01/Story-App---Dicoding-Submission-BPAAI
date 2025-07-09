package com.dicoding.storyapp.utils

import android.text.TextUtils
import android.util.Patterns

fun String.isValidEmail(): Boolean {
    return !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    if (isEmpty()) return false
    if (length < 8) return false

    val passwordHasUpperCase = this.any { it.isUpperCase() }
    val passwordHasLowerCase = this.any { it.isLowerCase() }
    val passwordHasDigit = this.any { it.isDigit() }

    return passwordHasUpperCase && passwordHasLowerCase && passwordHasDigit
}