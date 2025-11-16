package com.example.androidapplication.models

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("_id")
    val id: String,
    @SerializedName("userId")
    val userId: String?,
    @SerializedName("userName")
    val userName: String?,
    @SerializedName("photoId")
    val photoId: String?,
    @SerializedName("message")
    val message: String,
    @SerializedName("type")
    val type: String?,
    @SerializedName("isRead")
    val isRead: Boolean,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("formattedDate")
    val formattedDate: String?,
    @SerializedName("formattedTime")
    val formattedTime: String?,
    @SerializedName("dateTime")
    val dateTime: String?
)

data class UnreadCountResponse(
    @SerializedName("count")
    val count: Int
)

