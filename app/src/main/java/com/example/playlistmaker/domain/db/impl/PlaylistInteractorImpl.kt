package com.example.playlistmaker.domain.db.impl

import android.net.Uri
import com.example.playlistmaker.data.db.repository.PlaylistCoverRepository
import com.example.playlistmaker.data.db.repository.PlaylistRepository
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class PlaylistInteractorImpl(
    private val playlistCoverRepository: PlaylistCoverRepository,
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {
    override suspend fun addPlaylist(name: String, description: String?, coverUri: Uri?) {
        val id = UUID.randomUUID().toString()
        val playlistCoverUri = coverUri?.let {
            playlistCoverRepository.savePlaylistCover(id, coverUri)
        }
        val playlist = Playlist(
            id = id,
            title = name,
            description = description,
            playlistCoverUri = playlistCoverUri
        )
        playlistRepository.addPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist, track: Track) {
        playlistRepository.updatePlaylist(playlist, track)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = playlistRepository.getPlaylists()
}
