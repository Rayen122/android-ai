package com.example.androidapplication.models.story

import android.graphics.Bitmap
import com.example.androidapplication.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

class StoryRepository {

    private val api = RetrofitClient.storyInstance

    suspend fun getStories(token: String): List<Story> = withContext(Dispatchers.IO) {
        val response = api.getStories("Bearer $token")

        val list = if (response.isSuccessful) response.body() ?: emptyList()
        else emptyList()

        // 🔥 CORRECTION ICI : remplacer localhost par 10.0.2.2
        return@withContext list.map { story ->
            story.copy(
                imageUrl = story.imageUrl.replace("localhost", "10.0.2.2")
            )
        }
    }

    suspend fun uploadStory(token: String, bitmap: Bitmap): Story? = withContext(Dispatchers.IO) {

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val bytes = baos.toByteArray()

        val reqBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), bytes)
        val part = MultipartBody.Part.createFormData("story", "story.jpg", reqBody)

        val response = api.uploadStory("Bearer $token", part)

        val uploaded = response.body()

        // 🔥 Correction pour l'image qu'on vient d'envoyer
        return@withContext uploaded?.copy(
            imageUrl = uploaded.imageUrl.replace("localhost", "10.0.2.2")
        )
    }
}

