package com.example.androidapplication.ui.avatargenerator

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.remote.GenerateAvatarRequest
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AvatarGeneratorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AvatarUiState>(AvatarUiState.Initial)
    val uiState: StateFlow<AvatarUiState> = _uiState

    fun onImageSelected(uri: Uri) {
        _uiState.value = AvatarUiState.ImageSelected(uri)
    }

    fun generateAvatar(context: Context, imageUri: Uri, style: String) {
        viewModelScope.launch {
            _uiState.value = AvatarUiState.Loading
            try {
                val base64Image = encodeImageToBase64(context, imageUri)
                if (base64Image == null) {
                    _uiState.value = AvatarUiState.Error("Failed to process image")
                    return@launch
                }

                val request = GenerateAvatarRequest(image = base64Image, style = style)
                
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.stableDiffusionInstance.generateAvatar("Bearer dummy", request)
                }

                if (response.isSuccessful && response.body() != null) {
                    val imageUrl = response.body()!!.imageUrl
                    _uiState.value = AvatarUiState.Success(imageUrl)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    _uiState.value = AvatarUiState.Error("Backend Error: $errorMsg")
                }

            } catch (e: Exception) {
                _uiState.value = AvatarUiState.Error(e.message ?: "Unknown exception")
            }
        }
    }

    private suspend fun encodeImageToBase64(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    fun reset() {
        _uiState.value = AvatarUiState.Initial
    }
}

sealed class AvatarUiState {
    object Initial : AvatarUiState()
    data class ImageSelected(val imageUri: Uri) : AvatarUiState()
    object Loading : AvatarUiState()
    data class Success(val imageUrl: String) : AvatarUiState()
    data class Error(val message: String) : AvatarUiState()
}
