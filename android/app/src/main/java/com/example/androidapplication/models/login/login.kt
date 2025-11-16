package com.example.androidapplication.models.login

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val status: String,
    val message: String,
    val accessToken: String
)
