package com.example.androidapplication.models.login

data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean

)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val userName: String,
    val userEmail: String
)
