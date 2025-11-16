package com.example.androidapplication.remote

import com.example.androidapplication.models.register.RegisterRequest
import com.example.androidapplication.models.register.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface UserAPI {
    @POST("auth/signup") // Update to match your backend route
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>
}
data class Message(
    val message: String
)
data class ForgotPasswordDto(
    val email: String
)