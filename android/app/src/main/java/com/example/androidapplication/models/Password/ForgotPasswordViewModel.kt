package com.example.androidapplication.models.Password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    fun sendResetLink(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.forgotPassword(ForgotPassword(email))
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Failed to send reset link.")
                }
            } catch (e: Exception) {
                onError("An error occurred: ${e.message}")
            }
        }

    }
}
