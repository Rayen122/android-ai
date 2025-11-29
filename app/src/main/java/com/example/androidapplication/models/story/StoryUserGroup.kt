package com.example.androidapplication.models.story

data class StoryUserGroup(
    val userId: String,
    val userName: String,
    val coverImageUrl: String,
    val stories: List<Story>
)

