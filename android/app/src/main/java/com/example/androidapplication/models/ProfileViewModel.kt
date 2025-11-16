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
            val token = getTokenFromPreferences() // Fetch the token from preferences
            if (token.isNullOrEmpty()) {
                _error.value = "JWT Token is missing. Please log in again."
            } else {
                try {
                    Log.d("ProfileViewModel", "JWT Token: $token") // Log token for debugging
                    // Send the token to the backend and get user data
                    val response: Response<UserDataResponse> = RetrofitClient.instance.getUserProfile("Bearer $token")

                    if (response.isSuccessful) {
                        _userData.value = response.body()  // Update user data if successful
                    } else {
                        _error.value = "Failed to fetch user data: ${response.message()}"  // Handle failure in response
                    }
                } catch (e: Exception) {
                    _error.value = "Failed to fetch user data: ${e.localizedMessage}"  // Handle error
                }
            }
        }
    }



    private fun getTokenFromPreferences(): String? {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("jwt_token", null)
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
