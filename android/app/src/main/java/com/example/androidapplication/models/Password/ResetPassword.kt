package com.example.androidapplication.models.Password

// ResetPasswordRequest.kt
data class ResetPasswordRequest(
    val newPassword: String,
    val resetToken: String
)

// ResetPasswordResponse.kt
data class ResetPasswordResponse(
    val message: String
)

