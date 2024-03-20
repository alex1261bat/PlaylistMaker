package com.example.playlistmaker.domain.model

data class Playlist(
    val id: String,
    val title: String,
    val description: String?,
    val playlistCoverUri: String?,
    val tracksIds: List<String> = listOf(),
    val tracksCount: Int = 0
)
