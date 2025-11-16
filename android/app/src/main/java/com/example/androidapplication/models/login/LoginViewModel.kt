package com.example.androidapplication.models.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.remote.RetrofitClient
import com.example.androidapplication.utils.saveTokenToPreferences
import com.example.androidapplication.utils.getTokenFromPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState

    // Function to handle user login
    fun loginUser(email: String, password: String, context: Context) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                // Make the API request
                val response = RetrofitClient.instance.loginUser(LoginRequest(email, password))

                // Log the response and the accessToken to ensure it's returned correctly
                Log.d("LoginViewModel", "Login successful. AccessToken: ${response.accessToken}")

                // Save the accessToken to SharedPreferences
                saveTokenToPreferences(context, response.accessToken)

                // Log the stored token to verify it's saved
                val storedToken = getTokenFromPreferences(context)
                Log.d("LoginViewModel", "Stored Token: $storedToken")

                _loginState.value = LoginState.Success("Login successful")
            } catch (e: Exception) {
                // Log any exceptions for debugging
                Log.e("LoginViewModel", "Login failed", e)

                _loginState.value = LoginState.Error("Login failed. Please try again.")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val message: String) : LoginState()
    data class Error(val error: String) : LoginState()
}
