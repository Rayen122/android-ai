// RegistrationViewModel.kt
package com.example.androidapplication.models.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.remote.RegisterRequest
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel : ViewModel() {

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun registerUser(name: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                // Call backend
                val response: RegisterResponse =
                    RetrofitClient.instance.registerUser(RegisterRequest(name, email, password))

                Log.d("RegistrationViewModel", "Backend Response: $response")

                // Check if the response contains a valid user ID
                if (!response.data._id.isNullOrEmpty()) {
                    _registrationState.value =
                        RegistrationState.Success(response.message)
                    onSuccess() // Navigate to login
                } else {
                    _registrationState.value =
                        RegistrationState.Error("Registration failed: Invalid response")
                }
            } catch (e: Exception) {
                Log.e("RegistrationViewModel", "Error during registration", e)
                _registrationState.value = RegistrationState.Error("Error: ${e.localizedMessage}")
            }
        }
    }
}

// Sealed class remains the same
sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    data class Success(val message: String) : RegistrationState()
    data class Error(val error: String) : RegistrationState()
}