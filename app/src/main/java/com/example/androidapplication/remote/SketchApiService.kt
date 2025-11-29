package com.example.androidapplication.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

data class SketchResponse(
    @SerializedName("url")
    val url: String
)

interface SketchApiService {
    @GET("sketch/search")
    suspend fun searchSketch(@Query("prompt") prompt: String): SketchResponse
}
