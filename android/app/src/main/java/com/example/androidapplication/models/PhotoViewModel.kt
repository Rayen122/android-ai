package com.example.androidapplication.models

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.remote.RetrofitClient
import com.example.androidapplication.utils.getTokenFromPreferences
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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
            val token = getTokenFromPreferences(getApplication<Application>())
            if (token.isNullOrEmpty()) {
                _error.value = "JWT Token is missing. Please log in again."
                _isLoading.value = false
                return@launch
            }

            try {
                val response: Response<List<Photo>> = RetrofitClient.photoInstance.getAllPhotos("Bearer $token")
                if (response.isSuccessful) {
                    _photos.value = response.body() ?: emptyList()
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

    fun getMyPhotos(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = getTokenFromPreferences(getApplication<Application>())
            if (token.isNullOrEmpty()) {
                _error.value = "JWT Token is missing. Please log in again."
                _isLoading.value = false
                return@launch
            }

            try {
                val response: Response<List<Photo>> = RetrofitClient.photoInstance.getAllPhotos("Bearer $token")
                if (response.isSuccessful) {
                    val allPhotos = response.body() ?: emptyList()
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
            val token = getTokenFromPreferences(getApplication<Application>())
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
                    _uploadSuccess.value = true
                    // Refresh photos after upload
                    getAllPhotos()
                } else {
                    _error.value = "Failed to upload photo: ${response.message()}"
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

