package com.example.androidapplication.models.artiste


data class ArtistListResponse(
    val artists: List<Artist>
)
data class Artist(
    val name: String,
    val style_description: String,
    val country: String,
    val famous_works: List<Artwork>,
    val image_url: String? = null
)

data class Artwork(
    val title: String,
    val year: String,
    val image_url: String
)