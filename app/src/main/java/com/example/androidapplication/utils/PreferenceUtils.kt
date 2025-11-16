package com.example.androidapplication.utils

import android.content.Context

// Function to get token from SharedPreferences
fun getTokenFromPreferences(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("jwt_token", null) // Replace "jwt_token" with your actual key
}
// Function to save token to SharedPreferences
fun saveTokenToPreferences(context: Context, token: String) {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("jwt_token", token).apply()
}

// Function to remove token from SharedPreferences
fun removeTokenFromPreferences(context: Context) {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().remove("jwt_token").apply()
}
