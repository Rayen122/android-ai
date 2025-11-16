package com.example.androidapplication.models

import androidx.lifecycle.ViewModel

class SharedInformationViewModel : ViewModel() {
    private val userInfo = mutableMapOf<String, Any>()

    /**
     * Add a new piece of information to the map if it doesn't exist.
     * Throws an exception if the key already exists to prevent overwriting.
     */
    fun addInfo(key: String, value: Any) {
        if (userInfo.containsKey(key)) {
            throw IllegalStateException("$key already exists! Use a different key or explicitly update it.")
        }
        userInfo[key] = value
    }

    fun getInfo(key: String): Any? {
        return userInfo[key]
    }

    fun getAllInfo(): Map<String, Any> {
        return userInfo
    }
}
