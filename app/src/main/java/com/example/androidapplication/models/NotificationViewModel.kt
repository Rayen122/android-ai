package com.example.androidapplication.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.models.login.getAccessToken
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> get() = _notifications

    private val _unreadCount = MutableLiveData<Int>()
    val unreadCount: LiveData<Int> get() = _unreadCount

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getMyNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            val token = getAccessToken(getApplication<Application>())
            if (token.isNullOrEmpty()) {
                _error.value = "JWT Token is missing. Please log in again."
                _isLoading.value = false
                return@launch
            }

            try {
                val response = RetrofitClient.notificationInstance.getMyNotifications("Bearer $token")
                if (response.isSuccessful) {
                    _notifications.value = response.body() ?: emptyList()
                    Log.d("NotificationViewModel", "Loaded ${_notifications.value?.size ?: 0} notifications")
                } else {
                    _error.value = "Failed to fetch notifications: ${response.message()}"
                    Log.e("NotificationViewModel", "Failed to fetch notifications: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Failed to fetch notifications: ${e.localizedMessage}"
                Log.e("NotificationViewModel", "Error fetching notifications", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUnreadCount() {
        viewModelScope.launch {
            val token = getAccessToken(getApplication<Application>())
            if (token.isNullOrEmpty()) {
                return@launch
            }

            try {
                val response = RetrofitClient.notificationInstance.getUnreadCount("Bearer $token")
                if (response.isSuccessful) {
                    _unreadCount.value = response.body()?.count ?: 0
                }
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error fetching unread count", e)
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            val token = getAccessToken(getApplication<Application>())
            if (token.isNullOrEmpty()) {
                return@launch
            }

            try {
                val response = RetrofitClient.notificationInstance.markAsRead("Bearer $token", notificationId)
                if (response.isSuccessful) {
                    // Refresh notifications and count
                    getMyNotifications()
                    getUnreadCount()
                }
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error marking notification as read", e)
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val token = getAccessToken(getApplication<Application>())
            if (token.isNullOrEmpty()) {
                return@launch
            }

            try {
                val response = RetrofitClient.notificationInstance.markAllAsRead("Bearer $token")
                if (response.isSuccessful) {
                    // Refresh notifications and count
                    getMyNotifications()
                    getUnreadCount()
                }
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error marking all as read", e)
            }
        }
    }

    fun clearError() {
        _error.value = ""
    }
}

