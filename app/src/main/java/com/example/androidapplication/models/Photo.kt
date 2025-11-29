package com.example.androidapplication.models

import com.google.gson.annotations.SerializedName

data class Photo(
    @SerializedName("_id")
    val id: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("userId")
    val userId: String?,
    @SerializedName("userName")
    val userName: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("likesCount")
    val likesCount: Int? = 0,
    @SerializedName("commentsCount")
    val commentsCount: Int? = 0,
    @SerializedName("isLiked")
    val isLiked: Boolean? = false
)

data class UploadPhotoResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("photo")
    val photo: Photo?
)

