package com.example.androidapplication.models

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.models.login.getAccessToken
import com.example.androidapplication.remote.RetrofitClient
import com.example.androidapplication.remote.StableDiffusionRequest
import kotlinx.coroutines.launch
import retrofit2.Response

data class GeneratedImage(
    val imageUrl: String? = null,
    val base64Image: String? = null,
    val prompt: String
)

class GenreraiViewModel(application: Application) : AndroidViewModel(application) {
    private val _generatedImages = MutableLiveData<List<GeneratedImage>>()
    val generatedImages: LiveData<List<GeneratedImage>> get() = _generatedImages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _selectedModel = MutableLiveData<String>("stable-diffusion-v1-5")
    val selectedModel: LiveData<String> get() = _selectedModel

    fun generateImage(prompt: String, model: String = "stable-diffusion-v1-5") {
        if (prompt.isBlank()) {
            _error.value = "Le prompt ne peut pas être vide"
            return
        }

        viewModelScope.launch {
            try {
                Log.d("GenreraiViewModel", "Starting generateImage for prompt: ${prompt.take(50)}")
                _isLoading.value = true
                _error.value = ""
                
                Log.d("GenreraiViewModel", "Getting access token...")
                val token = try {
                    getAccessToken(getApplication<Application>())
                } catch (e: Exception) {
                    Log.e("GenreraiViewModel", "Error getting access token", e)
                    null
                }
                Log.d("GenreraiViewModel", "Token retrieved: ${if (token.isNullOrEmpty()) "null/empty" else "present"}")

                if (token.isNullOrEmpty()) {
                    Log.e("GenreraiViewModel", "No access token available")
                    _error.value = "Vous devez être connecté pour générer des images. Veuillez vous connecter."
                    _isLoading.value = false
                    return@launch
                }

                Log.d("GenreraiViewModel", "Creating request...")
                val request = StableDiffusionRequest(
                    prompt = prompt,
                    num_inference_steps = 20,
                    guidance_scale = 7.5f,
                    width = 512,
                    height = 512,
                    model = model
                )
                Log.d("GenreraiViewModel", "Request created: prompt=${request.prompt}, model=${request.model}")

                Log.d("GenreraiViewModel", "Calling API...")
                val response: Response<com.example.androidapplication.remote.StableDiffusionResponse> = 
                    RetrofitClient.stableDiffusionInstance.generateImage(
                        "Bearer $token",
                        request
                    )
                Log.d("GenreraiViewModel", "API call completed. Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    Log.d("GenreraiViewModel", "Response is successful, parsing body...")
                    
                    val responseBody = try {
                        val body = response.body()
                        Log.d("GenreraiViewModel", "Response body parsed successfully. image_urls: ${body?.image_urls?.size ?: 0}, images: ${body?.images?.size ?: 0}")
                        body
                    } catch (e: Exception) {
                        Log.e("GenreraiViewModel", "Error parsing response body with Gson", e)
                        e.printStackTrace()
                        _error.value = "Erreur lors du parsing de la réponse: ${e.message}"
                        _isLoading.value = false
                        return@launch
                    }
                    
                    if (responseBody == null) {
                        Log.e("GenreraiViewModel", "Response body is null")
                        _error.value = "Réponse vide du serveur"
                        _isLoading.value = false
                        return@launch
                    }
                    
                    Log.d("GenreraiViewModel", "Response body parsed. image_urls: ${responseBody.image_urls?.size ?: 0}, images: ${responseBody.images?.size ?: 0}")
                    val images = mutableListOf<GeneratedImage>()

                    // Handle base64 images
                    responseBody?.images?.forEach { base64Image ->
                        images.add(GeneratedImage(
                            base64Image = base64Image,
                            prompt = prompt
                        ))
                    }

                    // Handle image URLs (reference images)
                    try {
                        val imageUrls = responseBody.image_urls
                        Log.d("GenreraiViewModel", "Processing image_urls: ${imageUrls?.size ?: 0} URLs")
                        
                        if (imageUrls != null) {
                            imageUrls.forEachIndexed { index, imageUrl ->
                                try {
                                    if (!imageUrl.isNullOrBlank()) {
                                        // Valider et nettoyer l'URL
                                        var fixedUrl = imageUrl.trim()
                                        fixedUrl = fixedUrl.replace("localhost", "10.0.2.2")
                                        
                                        // Vérifier que l'URL est valide
                                        if (fixedUrl.startsWith("http://") || fixedUrl.startsWith("https://")) {
                                            images.add(GeneratedImage(
                                                imageUrl = fixedUrl,
                                                prompt = prompt
                                            ))
                                            Log.d("GenreraiViewModel", "Added image URL $index: ${fixedUrl.take(80)}...")
                                        } else {
                                            Log.w("GenreraiViewModel", "Invalid URL format at index $index: $fixedUrl")
                                        }
                                    } else {
                                        Log.w("GenreraiViewModel", "Empty URL at index $index")
                                    }
                                } catch (e: Exception) {
                                    Log.e("GenreraiViewModel", "Error processing image URL at index $index: $imageUrl", e)
                                }
                            }
                        } else {
                            Log.w("GenreraiViewModel", "image_urls is null in response")
                        }
                    } catch (e: Exception) {
                        Log.e("GenreraiViewModel", "Error processing image_urls array", e)
                    }

                    if (images.isNotEmpty()) {
                        // Replace all images with new search results
                        Log.d("GenreraiViewModel", "Successfully processed ${images.size} image URLs")
                        images.forEachIndexed { index, img ->
                            Log.d("GenreraiViewModel", "Image $index: ${img.imageUrl?.take(100) ?: "base64"}")
                        }
                        try {
                            _generatedImages.value = images
                            Log.d("GenreraiViewModel", "Updated generatedImages LiveData with ${images.size} images")
                        } catch (e: Exception) {
                            Log.e("GenreraiViewModel", "Error setting generatedImages value", e)
                            _error.value = "Erreur lors de l'affichage des images"
                        }
                    } else {
                        // Check if there's an error in the response
                        val errorMsg = responseBody?.error
                        if (errorMsg != null) {
                            _error.value = "Erreur: $errorMsg"
                        } else {
                            _error.value = "Aucune image de référence trouvée pour ce prompt."
                        }
                        Log.w("GenreraiViewModel", "No images in response. Response body: $responseBody")
                    }
                } else {
                    Log.e("GenreraiViewModel", "Response is not successful. Code: ${response.code()}, Message: ${response.message()}")
                    val errorBody = try {
                        response.errorBody()?.string()
                    } catch (e: Exception) {
                        Log.e("GenreraiViewModel", "Error reading error body", e)
                        null
                    }
                    _error.value = "Échec de la recherche (${response.code()}): ${response.message() ?: "Erreur inconnue"}"
                    Log.e("GenreraiViewModel", "Search failed: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                Log.e("GenreraiViewModel", "Error searching images", e)
                e.printStackTrace() // Stack trace complet pour déboguer
                
                // Message d'erreur plus détaillé
                val errorMsg = when {
                    e.message?.contains("Network") == true -> "Erreur réseau. Vérifiez votre connexion."
                    e.message?.contains("timeout") == true -> "Timeout. Le serveur met trop de temps à répondre."
                    e.message?.contains("UnknownHost") == true -> "Impossible de joindre le serveur."
                    else -> "Erreur lors de la recherche: ${e.localizedMessage ?: e.message ?: "Erreur inconnue"}"
                }
                
                _error.value = errorMsg
                // En cas d'erreur, s'assurer que l'état de chargement est réinitialisé
                _generatedImages.value = emptyList()
            } catch (e: Throwable) {
                // Capturer toutes les autres erreurs (OutOfMemoryError, etc.)
                Log.e("GenreraiViewModel", "Critical error: ${e.javaClass.simpleName}", e)
                e.printStackTrace()
                _error.value = "Erreur critique: ${e.javaClass.simpleName}. Redémarrez l'application."
                _generatedImages.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setModel(model: String) {
        _selectedModel.value = model
    }

    fun clearError() {
        _error.value = ""
    }

    fun clearImages() {
        _generatedImages.value = emptyList()
    }

    fun decodeBase64Image(base64String: String): Bitmap? {
        return try {
            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            Log.e("GenreraiViewModel", "Error decoding base64 image", e)
            null
        }
    }
}

