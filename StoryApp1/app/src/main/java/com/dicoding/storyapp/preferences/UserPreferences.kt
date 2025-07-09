package com.dicoding.storyapp.preferences

import android.content.Context
import com.dicoding.storyapp.model.UserModel
import androidx.core.content.edit

class UserPreferences(context: Context) {
    companion object {
        private const val NAME = "name"
        private const val USERID = "userid"
        private const val TOKEN = "token"
        private const val isLogin = "isLogin"
    }

    private val sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveSession(login: UserModel) {
        sharedPreferences.edit {
            putString(NAME, login.name)
            putString(USERID, login.userId)
            putString(TOKEN, login.token)
            putBoolean(isLogin, true)
        }
    }

    fun getSession() : UserModel {
        val model = UserModel()
        model.name = sharedPreferences.getString(NAME, null)
        model.userId = sharedPreferences.getString(USERID, null)
        model.token = sharedPreferences.getString(TOKEN, null)

        return model
    }

    fun logoutSession() {
        sharedPreferences.edit {
            clear()
            remove(TOKEN)
            putBoolean(isLogin, false)
        }
    }
}