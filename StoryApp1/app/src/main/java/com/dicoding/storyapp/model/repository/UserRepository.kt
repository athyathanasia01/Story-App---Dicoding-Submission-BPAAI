package com.dicoding.storyapp.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.storyapp.network.api.ApiService
import com.dicoding.storyapp.network.response.LoginResponse
import com.dicoding.storyapp.network.Result
import com.dicoding.storyapp.network.response.AddStoryResponse
import com.dicoding.storyapp.network.response.DetailStoryResponse
import com.dicoding.storyapp.network.response.ListStoryResponse
import com.dicoding.storyapp.network.response.RegisterResponse
import com.dicoding.storyapp.utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.lang.Exception

class UserRepository (
    private val apiService: ApiService
) {
    fun userLogin(
        email: String,
        password: String
    ) : LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            if (response.error) {
                emit(Result.Failed(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (e : Exception) {
            emit(Result.Failed(e.message.toString()))
        }
    }

    fun userRegister(
        name: String,
        email: String,
        password: String
    ) : LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            if (response.error) {
                emit(Result.Failed(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            emit(Result.Failed(e.message.toString()))
        }
    }

    fun getListStories(
        token: String
    ) : LiveData<Result<ListStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getListStories("Bearer $token")
            if (response.error) {
                emit(Result.Failed(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            emit(Result.Failed(e.message.toString()))
        }
    }

    fun addNewStory(
        token: String,
        description: String,
        imageStory: File
    ) : LiveData<Result<AddStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val reduceSize = imageStory.reduceFileImage()
            val imageStory = reduceSize.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart = MultipartBody.Part.createFormData(
                "photo",
                reduceSize.name,
                imageStory
            )
            val response = apiService.addNewStory(
                "Bearer $token",
                description.toRequestBody("text/plain".toMediaType()),
                imageMultipart
            )
            if (response.error) {
                emit(Result.Failed(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            emit(Result.Failed(e.message.toString()))
        }
    }

    fun DetailStory(
        id: String,
        token: String
    ) : LiveData<Result<DetailStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailStory(
                id,
                "Bearer $token"
            )
            if (response.error) {
                emit(Result.Failed(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (e : Exception) {
            emit(Result.Failed(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance : UserRepository? = null

        fun getRespositoryInstance(apiService: ApiService): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(apiService)
            }.also { instance = it }
        }
    }
}