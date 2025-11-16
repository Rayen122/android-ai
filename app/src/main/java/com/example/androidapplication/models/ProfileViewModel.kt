package com.example.androidapplication.models

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.models.login.getAccessToken
import com.example.androidapplication.models.logout.LogoutRequest
import com.example.androidapplication.remote.ApiService
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Response

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val _logoutMessage = MutableLiveData<String>()
    val logoutMessage: LiveData<String> get() = _logoutMessage

    private val _userData = MutableLiveData<UserDataResponse>()
    val userData: LiveData<UserDataResponse> get() = _userData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error


    // Fetch user data using the token
    fun fetchUserData() {
        viewModelScope.launch {
            val token = getAccessToken(getApplication<Application>()) // Fetch the token from preferences
            if (token.isNullOrEmpty()) {
                _error.value = "JWT Token is missing. Please log in again."
                Log.e("ProfileViewModel", "JWT Token is missing")
            } else {
                try {
                    Log.d("ProfileViewModel", "Fetching user data from backend...")
                    // Send the token to the backend and get user data
                    val response: Response<UserDataResponse> = RetrofitClient.instance.getUserProfile("Bearer $token")

                    if (response.isSuccessful) {
                        val userData = response.body()
                        if (userData != null) {
                            _userData.value = userData  // Update user data if successful
                            Log.d("ProfileViewModel", "User data loaded successfully. Name: ${userData.name}, Email: ${userData.email}")
                        } else {
                            _error.value = "User data is null"
                            Log.e("ProfileViewModel", "User data is null")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        _error.value = "Failed to fetch user data: ${response.message()}"
                        Log.e("ProfileViewModel", "Failed to fetch user data: ${response.code()} - $errorBody")
                    }
                } catch (e: Exception) {
                    _error.value = "Failed to fetch user data: ${e.localizedMessage}"
                    Log.e("ProfileViewModel", "Error fetching user data", e)
                }
            }
        }
    }





    /*fun logout(refreshToken: String) {
        viewModelScope.launch {
            try {
                val response = ApiService.logout(LogoutRequest(refreshToken))
                if (response.isSuccessful) {
                    logoutMessage.value = response.body()?.message ?: "Logout successful"
                    clearToken() // Clear the token after successful logout
                } else {
                    logoutMessage.value = "Logout failed: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                logoutMessage.value = "Unexpected error: ${e.localizedMessage}"
            }
        }
    }*/
}
