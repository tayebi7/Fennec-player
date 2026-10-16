package com.fennecplayer.app.data

data class PlaylistItem(
    val id: String,
    val name: String,
    val url: String,
    val logo: String?,
    val category: String,
    val type: ContentType
)

enum class ContentType {
    LIVE, MOVIE, SERIES
}

data class Category(
    val id: String,
    val name: String,
    val type: ContentType
)
