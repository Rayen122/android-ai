package com.example.androidapplication.models.artcritic

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ArtCriticViewModel : ViewModel() {

    private val _analysisResult = MutableStateFlow<AICriticReply?>(null)
    val analysisResult: StateFlow<AICriticReply?> = _analysisResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Convert Bitmap → Base64
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }

    // Send to backend
    fun sendToArtCritic(bitmap: Bitmap, question: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val base64 = bitmapToBase64(bitmap)

                val request = AICriticRequest(
                    imageBase64 = base64,
                    question = question            // 🔥 ajouté !
                )

                val response = RetrofitClient.artCriticInstance.analyzeArt(request)

                _analysisResult.value = response

            } catch (e: Exception) {
                _errorMessage.value = "Erreur : ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

