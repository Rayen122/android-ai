package com.example.androidapplication.ui.screen.sketch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SketchViewModel : ViewModel() {
    private val _sketches = MutableStateFlow<List<String>>(emptyList())
    val sketches: StateFlow<List<String>> = _sketches

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun searchSketch(prompt: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.sketchInstance.searchSketch(prompt)
                // The API returns a single URL object, but our UI might expect a list for future extensibility
                // For now, we just wrap the single result in a list
                _sketches.value = listOf(response.url)
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
