package com.example.androidapplication.models

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.androidapplication.models.login.getAccessToken
import com.example.androidapplication.remote.MagicUpgradeRequest
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MagicUpgradeViewModel(application: Application) : AndroidViewModel(application) {

    private val _isMagicUpgrading = MutableLiveData<Boolean>()
    val isMagicUpgrading: LiveData<Boolean> get() = _isMagicUpgrading

    private val _magicError = MutableLiveData<String>()
    val magicError: LiveData<String> get() = _magicError

    fun magicUpgrade(photoId: String, bitmap: Bitmap, context: Context, userId: String, onSuccess: (Bitmap) -> Unit) {
        viewModelScope.launch {
            _isMagicUpgrading.value = true
            _magicError.value = ""
            try {
                // 1. Convert bitmap to Base64
                val base64Image = withContext(Dispatchers.Default) {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                    val byteArray = byteArrayOutputStream.toByteArray()
                    Base64.encodeToString(byteArray, Base64.NO_WRAP)
                }

                // 2. Call Magic Upgrade API
                val token = getAccessToken(getApplication<Application>())
                if (token.isNullOrEmpty()) {
                    _magicError.value = "JWT Token is missing."
                    return@launch
                }

                val request = MagicUpgradeRequest(image = "data:image/jpeg;base64,$base64Image")
                
                // Network call should be on IO
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.stableDiffusionInstance.magicUpgrade("Bearer $token", request)
                }

                if (response.isSuccessful && response.body() != null) {
                    var newImageUrl = response.body()!!.imageUrl
                    Log.d("MagicUpgradeViewModel", "Received URL: $newImageUrl")
                    
                    if (newImageUrl.contains("localhost")) {
                        newImageUrl = newImageUrl.replace("localhost", "10.0.2.2")
                    }
                    
                    // 3. Download the new image as Bitmap on IO thread
                    withContext(Dispatchers.IO) {
                        try {
                            Log.d("MagicUpgradeViewModel", "Downloading image from: $newImageUrl")
                            val url = URL(newImageUrl)
                            val connection = url.openConnection()
                            connection.connect()
                            val input = connection.getInputStream()
                            val newBitmap = BitmapFactory.decodeStream(input)
                            
                            if (newBitmap != null) {
                                Log.d("MagicUpgradeViewModel", "Image downloaded successfully via URL connection")
                                withContext(Dispatchers.Main) {
                                    onSuccess(newBitmap)
                                }
                            } else {
                                Log.e("MagicUpgradeViewModel", "Failed to decode stream to Bitmap")
                                withContext(Dispatchers.Main) {
                                    _magicError.value = "Impossible de décoder l'image générée"
                                }
                            }
                        } catch (e: Exception) {
                             Log.e("MagicUpgradeViewModel", "Exception downloading image", e)
                             withContext(Dispatchers.Main) {
                                 _magicError.value = "Erreur de téléchargement: ${e.message}"
                             }
                        }
                    }
                } else {
                    Log.e("MagicUpgradeViewModel", "Magic Upgrade failed: ${response.errorBody()?.string()}")
                    _magicError.value = "Magic Upgrade failed"
                }
            } catch (e: Exception) {
                Log.e("MagicUpgradeViewModel", "Magic Upgrade error", e)
                _magicError.value = "Magic Upgrade error: ${e.localizedMessage}"
            } finally {
                _isMagicUpgrading.value = false
            }
        }
    }

    fun updatePhoto(photoId: String, bitmap: Bitmap, title: String?, description: String?, userId: String) {
        viewModelScope.launch {
            _isMagicUpgrading.value = true
            val token = getAccessToken(getApplication<Application>())
            if (token.isNullOrEmpty()) {
                _magicError.value = "JWT Token is missing."
                _isMagicUpgrading.value = false
                return@launch
            }

            try {
                // 1. Delete the old photo
                Log.d("MagicUpgradeViewModel", "Deleting old photo $photoId before update...")
                val deleteResponse = RetrofitClient.portfolioInstance.deletePortfolioItem("Bearer $token", photoId)
                
                if (!deleteResponse.isSuccessful) {
                    val photoDeleteResponse = RetrofitClient.photoInstance.deletePhoto("Bearer $token", photoId)
                    if (!photoDeleteResponse.isSuccessful) {
                        Log.w("MagicUpgradeViewModel", "Failed to delete old photo, but proceeding with upload anyway.")
                    }
                }

                // 2. Upload the new photo
                Log.d("MagicUpgradeViewModel", "Uploading new version of photo...")
                
                val file = bitmapToFile(bitmap, getApplication())
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)
                val titleBody = title?.takeIf { it.isNotEmpty() }?.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionBody = description?.takeIf { it.isNotEmpty() }?.toRequestBody("text/plain".toMediaTypeOrNull())
                val isPortfolioBody = "true".toRequestBody("text/plain".toMediaTypeOrNull())

                val uploadResponse = RetrofitClient.photoInstance.uploadPhoto(
                    "Bearer $token",
                    photoPart,
                    titleBody,
                    descriptionBody,
                    isPortfolioBody
                )

                if (uploadResponse.isSuccessful) {
                    Log.d("MagicUpgradeViewModel", "Photo updated successfully")
                    // We might need to trigger a refresh in other ViewModels, but for now this completes the action.
                } else {
                    val errorBody = uploadResponse.errorBody()?.string()
                    _magicError.value = "Failed to upload new version: ${uploadResponse.message()}"
                    Log.e("MagicUpgradeViewModel", "Update failed: ${uploadResponse.code()} - $errorBody")
                }

            } catch (e: Exception) {
                _magicError.value = "Failed to update photo: ${e.localizedMessage}"
                Log.e("MagicUpgradeViewModel", "Error updating photo", e)
            } finally {
                _isMagicUpgrading.value = false
            }
        }
    }

    fun deletePhoto(photoId: String, userId: String) {
        viewModelScope.launch {
            val token = getAccessToken(getApplication<Application>())
            if (token.isNullOrEmpty()) {
                _magicError.value = "JWT Token is missing."
                return@launch
            }

            try {
                Log.d("MagicUpgradeViewModel", "Deleting photo $photoId...")
                val deleteResponse = RetrofitClient.portfolioInstance.deletePortfolioItem("Bearer $token", photoId)
                
                if (deleteResponse.isSuccessful) {
                    Log.d("MagicUpgradeViewModel", "Photo deleted successfully from portfolio")
                    // Trigger refresh or handle success
                } else {
                    // Fallback to photo delete
                     val photoDeleteResponse = RetrofitClient.photoInstance.deletePhoto("Bearer $token", photoId)
                     if (photoDeleteResponse.isSuccessful) {
                         Log.d("MagicUpgradeViewModel", "Photo deleted successfully from photos")
                     } else {
                         val errorBody = photoDeleteResponse.errorBody()?.string()
                         _magicError.value = "Failed to delete photo: ${photoDeleteResponse.message()}"
                         Log.e("MagicUpgradeViewModel", "Delete failed: ${photoDeleteResponse.code()} - $errorBody")
                     }
                }
            } catch (e: Exception) {
                _magicError.value = "Failed to delete photo: ${e.localizedMessage}"
                Log.e("MagicUpgradeViewModel", "Error deleting photo", e)
            }
        }
    }

    private fun bitmapToFile(bitmap: Bitmap, context: Context): File {
        val file = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
        java.io.FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
        }
        return file
    }
}
