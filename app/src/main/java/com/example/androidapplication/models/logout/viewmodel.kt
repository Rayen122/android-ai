package com.example.androidapplication.models.logout


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LogoutViewModel : ViewModel() {

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> get() = _logoutState

    fun logout(refreshToken: String, context: Context) {
        viewModelScope.launch {
            try {
                // Try to logout from backend, but clear tokens even if it fails
                try {
                    val response = RetrofitClient.instance.logout(LogoutRequest(refreshToken))
                    if (!response.isSuccessful) {
                        android.util.Log.w("LogoutViewModel", "Backend logout failed, but clearing local tokens anyway")
                    }
                } catch (e: Exception) {
                    android.util.Log.w("LogoutViewModel", "Backend logout error: ${e.message}, but clearing local tokens anyway")
                }
                
                // Always clear tokens locally (including remember me token)
                clearTokens(context)
                _logoutState.value = LogoutState.Success("Logged out successfully")
            } catch (e: Exception) {
                // Even if there's an error, try to clear tokens
                clearTokens(context)
                _logoutState.value = LogoutState.Success("Logged out successfully")
            }
        }
    }
}
fun clearTokens(context: Context) {
    // Clear auth_prefs (accessToken, refreshToken, remember me)
    val authPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    authPrefs.edit().clear().apply()
    
    // Also clear app_prefs if tokens are stored there
    val appPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    appPrefs.edit().remove("jwt_token").apply()
    
    android.util.Log.d("LogoutViewModel", "All tokens cleared from SharedPreferences")
}

sealed class LogoutState {
    object Idle : LogoutState()
    data class Success(val message: String) : LogoutState()
    data class Error(val error: String) : LogoutState()
}
