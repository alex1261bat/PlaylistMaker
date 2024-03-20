package com.example.playlistmaker.data.db.repository

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist, track: Track)
    fun getPlaylists(): Flow<List<Playlist>>
}
