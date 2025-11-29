package com.example.androidapplication.models

import com.google.gson.annotations.SerializedName

data class Like(
    @SerializedName("_id")
    val id: String,
    @SerializedName("photoId")
    val photoId: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("createdAt")
    val createdAt: String
)

data class LikeResponse(
    val liked: Boolean,
    val likesCount: Int
)

