package com.example.androidapplication.models.story

data class StoryUser(
    val _id: String,
    val name: String,
    val email: String,
    val profileImageUrl: String? = null

)

data class Story(
    val _id: String,
    val imageUrl: String,
    val userId: StoryUser,
    val createdAt: String,
    val expiresAt: String,
)

