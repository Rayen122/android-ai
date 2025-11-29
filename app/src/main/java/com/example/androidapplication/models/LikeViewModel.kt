package com.example.androidapplication.models

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.models.login.getAccessToken
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LikeViewModel : ViewModel() {
    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked

    private val _likesCount = MutableStateFlow(0)
    val likesCount: StateFlow<Int> = _likesCount

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun checkLikeStatus(photoId: String, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = getAccessToken(context)
                if (token == null) {
                    Log.e("LikeViewModel", "Token is null")
                    _error.value = "Authentication required"
                    _isLoading.value = false
                    return@launch
                }
                Log.d("LikeViewModel", "Checking like status for photo: $photoId")
                Log.d("LikeViewModel", "Full URL would be: http://10.0.2.2:3000/photos/$photoId/like-status")
                Log.d("LikeViewModel", "Token: ${token.take(20)}...")
                val response = RetrofitClient.likeInstance.checkLike("Bearer $token", photoId)
                Log.d("LikeViewModel", "Response code: ${response.code()}, message: ${response.message()}")
                if (response.isSuccessful) {
                    val likeResponse = response.body()
                    likeResponse?.let {
                        _isLiked.value = it.liked
                        _likesCount.value = it.likesCount
                        Log.d("LikeViewModel", "Like status checked - liked: ${it.liked}, count: ${it.likesCount}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LikeViewModel", "Failed to check like status: ${response.code()} - ${response.message()}")
                    Log.e("LikeViewModel", "Error body: $errorBody")
                    _error.value = "Failed to check like status: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("LikeViewModel", "Error checking like status", e)
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleLike(photoId: String, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = getAccessToken(context)
                if (token == null) {
                    Log.e("LikeViewModel", "Token is null")
                    _error.value = "Authentication required"
                    _isLoading.value = false
                    return@launch
                }
                Log.d("LikeViewModel", "Toggling like for photo: $photoId")
                Log.d("LikeViewModel", "Full URL would be: http://10.0.2.2:3000/photos/$photoId/like")
                Log.d("LikeViewModel", "Token: ${token.take(20)}...")
                val response = RetrofitClient.likeInstance.toggleLike("Bearer $token", photoId)
                Log.d("LikeViewModel", "Response code: ${response.code()}, message: ${response.message()}")
                if (response.isSuccessful) {
                    val likeResponse = response.body()
                    likeResponse?.let {
                        _isLiked.value = it.liked
                        _likesCount.value = it.likesCount
                        Log.d("LikeViewModel", "Like toggled - liked: ${it.liked}, count: ${it.likesCount}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LikeViewModel", "Failed to toggle like: ${response.code()} - ${response.message()}")
                    Log.e("LikeViewModel", "Error body: $errorBody")
                    _error.value = "Failed to toggle like: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("LikeViewModel", "Error toggling like", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}

