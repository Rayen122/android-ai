package com.example.androidapplication.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleAuthService(private val context: Context) {

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("792166898260-06j0nm5b8egq9ju2rj9ai1m38539p9ut.apps.googleusercontent.com")
        .requestEmail()
        .build()

    private val googleClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    fun getSignInIntent(): Intent {
        return googleClient.signInIntent
    }

    fun extractToken(data: Intent): String {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = task.result
        return account.idToken ?: throw Exception("Token Google introuvable")
    }
}

