package com.example.playlistmaker.domain.db

import android.net.Uri
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun addPlaylist(name: String, description: String?, coverUri: Uri?)
    suspend fun updatePlaylist(playlist: Playlist, track: Track)
    fun getPlaylists(): Flow<List<Playlist>>
}
