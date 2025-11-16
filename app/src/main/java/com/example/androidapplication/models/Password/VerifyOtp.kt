package com.example.androidapplication.models.Password

data class VerifyOtpRequest(
    val recoveryCode: String
)

data class VerifyOtpResponse(
    val resetToken: String,
)

