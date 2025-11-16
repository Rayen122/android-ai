package com.example.androidapplication.models.logout

data class LogoutRequest(
    val refreshToken: String
)

// Modèle pour recevoir la réponse de l'API
data class LogoutResponse(
    val message: String
)