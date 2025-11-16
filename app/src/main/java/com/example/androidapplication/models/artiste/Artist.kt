package com.example.androidapplication.models.artiste


data class ArtistListResponse(
    val artists: List<Artist>
)
data class Artist(
    val name: String,
    val style_description: String,
    val country: String,
    val famous_works: List<String>
)