package com.example.androidapplication.models.artiste

// ArtistViewModel.kt

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ArtistViewModel : ViewModel() {
    val artists = mutableStateOf<List<Artist>>(emptyList())
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    val currentTheme = mutableStateOf("") // Default theme - empty for "All"
    private var hasLoadedInitial = false

    fun fetchArtists(query: String, isNameSearch: Boolean = false) {
        currentTheme.value = if (isNameSearch) "" else query
        isLoading.value = true
        error.value = null
        viewModelScope.launch {
            try {
                Log.d("ArtistViewModel", "Fetching artists with query: '$query', isNameSearch: $isNameSearch")
                val response = if (isNameSearch) {
                    RetrofitClient.instance.getArtists(null, query)
                } else {
                    RetrofitClient.instance.getArtists(query, null)
                }
                Log.d("ArtistViewModel", "Successfully fetched ${response.artists.size} artists")
                artists.value = response.artists
                hasLoadedInitial = true
            } catch (e: SocketTimeoutException) {
                val errorMsg = "Connection timeout. Please check your internet connection and try again."
                error.value = errorMsg
                Log.e("ArtistViewModel", "Timeout error: ${e.message}", e)
                println("Error fetching artists: timeout")
            } catch (e: UnknownHostException) {
                val errorMsg = "Cannot reach server. Please check your connection."
                error.value = errorMsg
                Log.e("ArtistViewModel", "Network error: ${e.message}", e)
            } catch (e: Exception) {
                val errorMsg = "Failed to load artists: ${e.localizedMessage ?: e.message}"
                error.value = errorMsg
                Log.e("ArtistViewModel", "Error fetching artists: ${e.message}", e)
                println("Error fetching artists: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }
    
    fun loadInitialArtists() {
        if (!hasLoadedInitial && !isLoading.value) {
            fetchArtists("")
        }
    }
}