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
                val response: Response<List<Photo>> = RetrofitClient.photoInstance.getAllPhotos("Bearer $token")
                if (response.isSuccessful) {
                    val allPhotos = response.body()?.map { photo ->
                        // Replace localhost with 10.0.2.2 for Android emulator
                        val fixedImageUrl = photo.imageUrl.replace("localhost", "10.0.2.2")
                        photo.copy(imageUrl = fixedImageUrl)
                    } ?: emptyList()
                    _myPhotos.value = allPhotos.filter { it.userId == userId }
                } else {
                    _error.value = "Failed to fetch photos: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Failed to fetch photos: ${e.localizedMessage}"
                Log.e("PhotoViewModel", "Error fetching photos", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadPhoto(bitmap: Bitmap, title: String?, description: String?) {
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

                val response: Response<UploadPhotoResponse> = RetrofitClient.photoInstance.uploadPhoto(
                    "Bearer $token",
                    photoPart,
                    titleBody,
                    descriptionBody
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
                    // Refresh photos after upload
                    getAllPhotos()
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
}

