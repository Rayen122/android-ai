package com.example.androidapplication.models.register


data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val data: UserData,
    val message: String
)

data class UserData(
    val _id: String,
    val name: String,
    val email: String
)