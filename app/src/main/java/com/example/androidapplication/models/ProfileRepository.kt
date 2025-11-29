package com.example.androidapplication.models

import android.content.Context
import android.graphics.Bitmap
import com.example.androidapplication.models.login.getAccessToken
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

class ProfileRepository {

    private val api = RetrofitClient.photoInstance   // ✅ TRÈS IMPORTANT

    suspend fun uploadProfileImage(bitmap: Bitmap, context: Context): Boolean =
        withContext(Dispatchers.IO) {

            val token = getAccessToken(context) ?: return@withContext false

            // Convertir Bitmap → JPEG
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val bytes = baos.toByteArray()

            val requestBody =
                RequestBody.create("image/jpeg".toMediaTypeOrNull(), bytes)

            // ⚠️ Le champ s'appelle **file**
            val filePart =
                MultipartBody.Part.createFormData(
                    "file",
                    "profile.jpg",
                    requestBody
                )

            val response = api.uploadProfileImage("Bearer $token", filePart)

            return@withContext response.isSuccessful
        }
}

