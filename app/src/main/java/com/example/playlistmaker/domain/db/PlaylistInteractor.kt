package com.example.playlistmaker.domain.db

import android.net.Uri
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun addPlaylist(name: String, description: String?, coverUri: Uri?)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)
    fun getPlaylistById(playlistId: String): Flow<Playlist>
    fun getPlaylistTracks(tracksIds: List<String>): Flow<List<Track>>
    fun saveTrackForPlaying(track: Track)
    suspend fun deleteTrackFromPlaylist(playlist: Playlist, trackId: String)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist, coverUri: Uri?)
}
