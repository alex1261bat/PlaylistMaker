package com.example.playlistmaker.ui.media

import com.example.playlistmaker.domain.model.Track

data class PlaylistDataScreenState(
    val playlistTitle: String,
    val playlistCoverUri: String?,
    val playlistDescription: String,
    val playlistDuration: Int,
    val tracksCount: Int,
    val tracks: List<Track>
)
