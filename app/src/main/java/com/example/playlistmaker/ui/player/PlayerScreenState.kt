package com.example.playlistmaker.ui.player

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.player.model.MediaPlayerStatus

data class PlayerScreenState (
    val playerState: MediaPlayerStatus = MediaPlayerStatus.STATE_PREPARED,
    val track: Track? = null,
    val trackTime: String = "",
    val isFavorite: Boolean = false
)
