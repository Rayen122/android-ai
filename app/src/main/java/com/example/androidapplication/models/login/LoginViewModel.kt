package com.example.androidapplication.models.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.remote.RetrofitClient
import com.example.androidapplication.utils.saveTokenToPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState
    fun loginUser(email: String, password: String, context: Context, rememberMe: Boolean) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = RetrofitClient.instance.loginUser(LoginRequest(email, password,rememberMe))
                Log.d("LoginViewModel", "Login successful. AccessToken: ${response.accessToken}")

                // Save tokens
                saveTokenToPreferences(context, response.accessToken, response.refreshToken, rememberMe)

                _loginState.value = LoginState.Success("Login successful")
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login failed", e)
                _loginState.value = LoginState.Error(
                    "Login failed: ${e.localizedMessage ?: "Unknown error"}"
                )
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
