package com.example.androidapplication.models

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("_id")
    val id: String,
    @SerializedName("photoId")
    val photoId: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("userName")
    val userName: String?,
    @SerializedName("text")
    val text: String,
    @SerializedName("createdAt")
    val createdAt: String
)

data class CommentRequest(
    val text: String
)

data class CommentResponse(
    val comment: Comment
)

