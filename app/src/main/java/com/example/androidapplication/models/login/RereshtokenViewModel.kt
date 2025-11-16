package com.example.androidapplication.models.login


import android.content.Context

fun saveTokenToPreferences(context: Context, accessToken: String, refreshToken: String, rememberMe: Boolean) {
    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    with(prefs.edit()) {
        putString("accessToken", accessToken)
        if (rememberMe) {
            putString("refreshToken", refreshToken)
        } else {
            remove("refreshToken")
        }
        apply()
    }
}

fun getAccessToken(context: Context): String? {
    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    return prefs.getString("accessToken", null)
}

fun getRefreshToken(context: Context): String? {
    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    return prefs.getString("refreshToken", null)
}
fun getSavedTokens(context: Context): Pair<String?, String?> {
    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    val accessToken = prefs.getString("accessToken", null)
    val refreshToken = prefs.getString("refreshToken", null)
    return Pair(accessToken, refreshToken)
}
