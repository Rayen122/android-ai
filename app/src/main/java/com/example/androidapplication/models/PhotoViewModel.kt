package com.example.androidapplication.models

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.models.login.getAccessToken
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class PhotoViewModel(application: Application) : AndroidViewModel(application) {
    private val _photos = MutableLiveData<List<Photo>>()
    val photos: LiveData<List<Photo>> get() = _photos

    private val _myPhotos = MutableLiveData<List<Photo>>()
    val myPhotos: LiveData<List<Photo>> get() = _myPhotos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _uploadSuccess = MutableLiveData<Boolean>()
    val uploadSuccess: LiveData<Boolean> get() = _uploadSuccess

    private val _isConvertingSketch = MutableLiveData<Boolean>()
    val isConvertingSketch: LiveData<Boolean> get() = _isConvertingSketch

    private val _sketchImageUrl = MutableLiveData<String?>()
    val sketchImageUrl: LiveData<String?> get() = _sketchImageUrl

    private val _convertedImageUrl = MutableLiveData<String?>()
    val convertedImageUrl: LiveData<String?> get() = _convertedImageUrl

    private var sketchConversionJob: kotlinx.coroutines.Job? = null

    fun getAllPhotos() {
        viewModelScope.launch {
            _isLoading.value = true
            val token = getAccessToken(getApplication<Application>())
            if (token.isNullOrEmpty()) {
                _error.value = "JWT Token is missing. Please log in again."
                _isLoading.value = false
                return@launch
            }

            try {
                val response: Response<List<Photo>> = RetrofitClient.photoInstance.getAllPhotos("Bearer $token")
                if (response.isSuccessful) {
                    val photosList = response.body()?.map { photo ->
                        // Replace localhost with 10.0.2.2 for Android emulator
                        val fixedImageUrl = photo.imageUrl.replace("localhost", "10.0.2.2")
                        photo.copy(imageUrl = fixedImageUrl)
                    } ?: emptyList()
                    Log.d("PhotoViewModel", "Loaded ${photosList.size} photos")
                    photosList.forEach { photo ->
                        Log.d("PhotoViewModel", "Photo URL: ${photo.imageUrl}")
                    }
                    _photos.value = photosList
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Failed to fetch photos: ${response.message()}"
                    Log.e("PhotoViewModel", "Failed to fetch photos: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _error.value = "Failed to fetch photos: ${e.localizedMessage}"
                Log.e("PhotoViewModel", "Error fetching photos", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMyPhotos(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = getAccessToken(getApplication<Application>())
            if (token.isNullOrEmpty()) {
                _error.value = "JWT Token is missing. Please log in again."
                _isLoading.value = false
                return@launch
            }

            try {
                // Fetch ALL photos (which excludes portfolio items by default in backend)
                val response: Response<List<Photo>> = RetrofitClient.photoInstance.getAllPhotos("Bearer $token")
                if (response.isSuccessful) {
                    val allPhotos = response.body()?.map { photo ->
                        // Replace localhost with 10.0.2.2 for Android emulator
                        val fixedImageUrl = photo.imageUrl.replace("localhost", "10.0.2.2")
                        photo.copy(imageUrl = fixedImageUrl)
                    } ?: emptyList()
                    // Filter to show only THIS user's posts
                    _myPhotos.value = allPhotos.filter { it.userId == userId }
                    Log.d("PhotoViewModel", "Loaded ${_myPhotos.value?.size} posts for user $userId")
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Failed to fetch photos: ${response.message()}"
                    Log.e("PhotoViewModel", "Failed to fetch photos: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _error.value = "Failed to fetch photos: ${e.localizedMessage}"
                Log.e("PhotoViewModel", "Error fetching photos", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

<<<<<<< HEAD
    private val _portfolioPhotos = MutableLiveData<List<Photo>>()
    val portfolioPhotos: LiveData<List<Photo>> get() = _portfolioPhotos

    fun getPortfolioPhotos(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = getAccessToken(getApplication<Application>())
            if (token.isNullOrEmpty()) {
                _error.value = "JWT Token is missing. Please log in again."
                _isLoading.value = false
                return@launch
            }

            try {
                // Use the dedicated portfolio endpoint
                val response: Response<List<Photo>> = RetrofitClient.portfolioInstance.getPortfolio("Bearer $token")
                if (response.isSuccessful) {
                    val pPhotos = response.body()?.map { photo ->
                        val fixedImageUrl = photo.imageUrl.replace("localhost", "10.0.2.2")
                        photo.copy(imageUrl = fixedImageUrl)
                    } ?: emptyList()
                    
                    Log.d("PhotoViewModel", "Loaded ${pPhotos.size} portfolio items")
                    _portfolioPhotos.value = pPhotos
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Failed to fetch portfolio: ${response.message()}"
                    Log.e("PhotoViewModel", "Failed to fetch portfolio: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _error.value = "Failed to fetch portfolio: ${e.localizedMessage}"
                Log.e("PhotoViewModel", "Error fetching portfolio", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePortfolioPhoto(photoId: String) {
        viewModelScope.launch {
            val token = getAccessToken(getApplication<Application>())
            if (token.isNullOrEmpty()) {
                _error.value = "JWT Token is missing. Please log in again."
                return@launch
            }

            try {
                // Optimistically remove from list immediately
                val currentList = _portfolioPhotos.value.orEmpty().toMutableList()
                val updatedList = currentList.filter { it.id != photoId }
                _portfolioPhotos.value = updatedList

                // Call backend
                val response = RetrofitClient.portfolioInstance.deletePortfolioItem("Bearer $token", photoId)
                
                if (!response.isSuccessful) {
                    // Revert if failed
                    _portfolioPhotos.value = currentList
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Failed to delete: ${response.message()}"
                    Log.e("PhotoViewModel", "Failed to delete portfolio item: ${response.code()} - $errorBody")
                } else {
                    Log.d("PhotoViewModel", "Portfolio item deleted successfully: $photoId")
                }
            } catch (e: Exception) {
                // Revert if error
                // We would need the original list here, but for simplicity we just log error
                // Ideally we should re-fetch or keep original list in a var
                _error.value = "Error deleting item: ${e.localizedMessage}"
                Log.e("PhotoViewModel", "Error deleting portfolio item", e)
            }
        }
    }

=======
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
    fun uploadPhoto(bitmap: Bitmap, title: String?, description: String?, isPortfolio: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _uploadSuccess.value = false
            val token = getAccessToken(getApplication<Application>())
            if (token.isNullOrEmpty()) {
                _error.value = "JWT Token is missing. Please log in again."
                _isLoading.value = false
                return@launch
            }

            try {
                // Convert bitmap to file
                val file = bitmapToFile(bitmap, getApplication())

                // Create multipart request body
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)

                val titleBody = title?.takeIf { it.isNotEmpty() }?.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionBody = description?.takeIf { it.isNotEmpty() }?.toRequestBody("text/plain".toMediaTypeOrNull())
                val isPortfolioBody = if (isPortfolio) "true".toRequestBody("text/plain".toMediaTypeOrNull()) else null

                val response: Response<UploadPhotoResponse> = RetrofitClient.photoInstance.uploadPhoto(
                    "Bearer $token",
                    titleBody,
                    descriptionBody,
<<<<<<< HEAD
                    isPortfolioBody,
                    isPortfolioBody, // Send same value for is_portfolio
                    photoPart
=======
                    isPortfolioBody
>>>>>>> d32fa832c5f99342b04ee59547cc09b7371be886
                )

                if (response.isSuccessful) {
                    val uploadedPhoto = response.body()?.photo?.let { photo ->
                        // Replace localhost with 10.0.2.2 for Android emulator
                        val fixedImageUrl = photo.imageUrl.replace("localhost", "10.0.2.2")
                        photo.copy(imageUrl = fixedImageUrl)
                    }
                    Log.d("PhotoViewModel", "Photo uploaded successfully: ${uploadedPhoto?.id}, URL: ${uploadedPhoto?.imageUrl}")
                    _uploadSuccess.value = true
                    // Add a small delay to let backend process the image
                    kotlinx.coroutines.delay(1000)
                    // Refresh appropriate list
                    if (isPortfolio) {
                        // We need the userId to refresh portfolio. 
                        // Since we don't have it passed here, we might rely on the UI to trigger refresh 
                        // or we could store userId in ViewModel. 
                        // For now, let's just log. The UI should observe uploadSuccess and refresh.
                        Log.d("PhotoViewModel", "Portfolio upload success")
                    } else {
                        getAllPhotos()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Failed to upload photo: ${response.message()}. $errorBody"
                    Log.e("PhotoViewModel", "Upload failed: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _error.value = "Failed to upload photo: ${e.localizedMessage}"
                Log.e("PhotoViewModel", "Error uploading photo", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveToPortfolio(bitmap: Bitmap, title: String, description: String) {
        uploadPhoto(bitmap, title, description, isPortfolio = true)
    }

    private fun bitmapToFile(bitmap: Bitmap, context: Context): File {
        val file = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
        }
        return file
    }

    fun clearError() {
        _error.value = ""
    }

    fun clearUploadSuccess() {
        _uploadSuccess.value = false
    }

    fun convertToSketch(photo: Photo) {
        // Annuler la conversion précédente si elle est en cours
        sketchConversionJob?.cancel()

        sketchConversionJob = viewModelScope.launch {
            _isConvertingSketch.value = true
            _error.value = ""
            _sketchImageUrl.value = null

            val token = getAccessToken(getApplication<Application>())
            if (token.isNullOrEmpty()) {
                _error.value = "Vous devez être connecté pour convertir la photo."
                _isConvertingSketch.value = false
                return@launch
            }

            try {
                Log.d("PhotoViewModel", "Converting photo ${photo.id} to sketch...")

                val response: retrofit2.Response<com.example.androidapplication.remote.ConvertToSketchResponse> =
                    RetrofitClient.photoInstance.convertToSketch(
                        "Bearer $token",
                        photo.id
                    )

                Log.d("PhotoViewModel", "API response: code=${response.code()}, isSuccessful=${response.isSuccessful}")

                if (response.isSuccessful) {
                    val responseBody = try {
                        response.body()
                    } catch (e: Exception) {
                        Log.e("PhotoViewModel", "Error parsing response body", e)
                        null
                    }

                    if (responseBody == null) {
                        Log.e("PhotoViewModel", "Response body is null")
                        _error.value = "Réponse vide du serveur"
                        return@launch
                    }

                    // Vérifier s'il y a une erreur dans la réponse
                    if (!responseBody.error.isNullOrBlank()) {
                        _error.value = "Erreur: ${responseBody.error}"
                        Log.e("PhotoViewModel", "Error in response: ${responseBody.error}")
                        return@launch
                    }

                    // Récupérer l'URL du croquis
                    val sketchUrl = responseBody.sketch_url
                    Log.d("PhotoViewModel", "Received sketch_url from backend: $sketchUrl")

                    if (sketchUrl != null && sketchUrl.isNotBlank()) {
                        val fixedUrl = sketchUrl.trim().replace("localhost", "10.0.2.2")
                        Log.d("PhotoViewModel", "Fixed URL (after localhost replacement): $fixedUrl")

                        // Vérifier que l'URL est valide
                        if (fixedUrl.startsWith("http://") || fixedUrl.startsWith("https://")) {
                            _sketchImageUrl.value = fixedUrl
                            Log.d("PhotoViewModel", "Sketch URL set successfully: $fixedUrl")
                            Log.d("PhotoViewModel", "sketchImageUrl LiveData value: ${_sketchImageUrl.value}")
                        } else {
                            _error.value = "URL de croquis invalide: $fixedUrl"
                            Log.e("PhotoViewModel", "Invalid sketch URL format: $fixedUrl")
                        }
                    } else {
                        _error.value = "Aucune image de croquis n'a pu être générée. Veuillez réessayer."
                        Log.w("PhotoViewModel", "No sketch URL found in response. Response body: $responseBody")
                    }
                } else {
                    val errorBody = try {
                        response.errorBody()?.string()
                    } catch (e: Exception) {
                        null
                    }
                    _error.value = "Échec de la conversion en croquis (${response.code()}): ${response.message() ?: "Erreur inconnue"}"
                    Log.e("PhotoViewModel", "Sketch conversion failed: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _error.value = "Erreur lors de la conversion: ${e.localizedMessage ?: e.message ?: "Erreur inconnue"}"
                Log.e("PhotoViewModel", "Error converting to sketch", e)
                e.printStackTrace()
            } finally {
                _isConvertingSketch.value = false
                sketchConversionJob = null
            }
        }
    }

    fun cancelSketchConversion() {
        sketchConversionJob?.cancel()
        sketchConversionJob = null
        _isConvertingSketch.value = false
        _error.value = "Conversion annulée"
        Log.d("PhotoViewModel", "Sketch conversion cancelled")
    }

    fun clearSketchImage() {
        _sketchImageUrl.value = null
        _convertedImageUrl.value = null
    }

    fun convertToWatercolor(photo: Photo) {
        convertImage(photo, "watercolor")
    }

    fun convertToVintage(photo: Photo) {
        convertImage(photo, "vintage")
    }

    fun convertToBlackAndWhite(photo: Photo) {
        convertImage(photo, "blackwhite")
    }

    fun convertToOilPainting(photo: Photo) {
        convertImage(photo, "oil")
    }

    private fun convertImage(photo: Photo, style: String) {
        sketchConversionJob?.cancel()

        sketchConversionJob = viewModelScope.launch {
            _isConvertingSketch.value = true
            _error.value = ""
            _sketchImageUrl.value = null
            _convertedImageUrl.value = null

            val token = getAccessToken(getApplication<Application>())
            if (token.isNullOrEmpty()) {
                _error.value = "Vous devez être connecté pour convertir la photo."
                _isConvertingSketch.value = false
                return@launch
            }

            try {
                Log.d("PhotoViewModel", "Converting photo ${photo.id} to $style...")

                val response: retrofit2.Response<com.example.androidapplication.remote.ConvertToSketchResponse> =
                    RetrofitClient.photoInstance.convertImage(
                        "Bearer $token",
                        photo.id,
                        style
                    )

                Log.d("PhotoViewModel", "API response: code=${response.code()}, isSuccessful=${response.isSuccessful}")

                if (response.isSuccessful) {
                    val responseBody = try {
                        response.body()
                    } catch (e: Exception) {
                        Log.e("PhotoViewModel", "Error parsing response body", e)
                        null
                    }

                    if (responseBody == null) {
                        Log.e("PhotoViewModel", "Response body is null")
                        _error.value = "Réponse vide du serveur"
                        return@launch
                    }

                    if (!responseBody.error.isNullOrBlank()) {
                        _error.value = "Erreur: ${responseBody.error}"
                        Log.e("PhotoViewModel", "Error in response: ${responseBody.error}")
                        return@launch
                    }

                    val convertedUrl = responseBody.sketch_url

                    if (convertedUrl != null && convertedUrl.isNotBlank()) {
                        val fixedUrl = convertedUrl.trim().replace("localhost", "10.0.2.2")

                        if (fixedUrl.startsWith("http://") || fixedUrl.startsWith("https://")) {
                            _convertedImageUrl.value = fixedUrl
                            _sketchImageUrl.value = fixedUrl // Utiliser la même variable pour l'affichage
                            Log.d("PhotoViewModel", "$style conversion successful: $fixedUrl")
                        } else {
                            _error.value = "URL invalide: $fixedUrl"
                            Log.e("PhotoViewModel", "Invalid URL: $fixedUrl")
                        }
                    } else {
                        _error.value = "Aucune image n'a pu être générée. Veuillez réessayer."
                        Log.w("PhotoViewModel", "No converted URL found in response")
                    }
                } else {
                    val errorBody = try {
                        response.errorBody()?.string()
                    } catch (e: Exception) {
                        null
                    }
                    _error.value = "Échec de la conversion (${response.code()}): ${response.message() ?: "Erreur inconnue"}"
                    Log.e("PhotoViewModel", "Conversion failed: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _error.value = "Erreur lors de la conversion: ${e.localizedMessage ?: e.message ?: "Erreur inconnue"}"
                Log.e("PhotoViewModel", "Error converting image", e)
                e.printStackTrace()
            } finally {
                _isConvertingSketch.value = false
                sketchConversionJob = null
            }
        }
    }
}

