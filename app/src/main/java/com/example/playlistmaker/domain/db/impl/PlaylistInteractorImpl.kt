package com.example.playlistmaker.domain.db.impl

import android.net.Uri
import com.example.playlistmaker.data.db.repository.PlaylistCoverRepository
import com.example.playlistmaker.data.db.repository.PlaylistRepository
import com.example.playlistmaker.data.search.SearchHistoryRepository
import com.example.playlistmaker.data.search.TrackRepository
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import java.util.UUID

class PlaylistInteractorImpl(
    private val playlistCoverRepository: PlaylistCoverRepository,
    private val playlistRepository: PlaylistRepository,
    private val trackRepository: TrackRepository,
    private val historyRepository: SearchHistoryRepository
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

    override fun getPlaylists(): Flow<List<Playlist>> = playlistRepository.getPlaylists()

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        playlistRepository.addTrackToPlaylist(playlist, track)
    }

    override fun getPlaylistById(playlistId: String): Flow<Playlist> {
        return playlistRepository.getPlaylistById(playlistId).filterNotNull()
    }

    override fun getPlaylistTracks(tracksIds: List<String>): Flow<List<Track>> {
        return playlistRepository.getPlaylistTracks(tracksIds)
    }

    override fun saveTrackForPlaying(track: Track) = historyRepository.saveTrackForPlaying(track)

    override suspend fun deleteTrackFromPlaylist(playlist: Playlist, trackId: String) {
        val newPlaylistTracksIds = playlist.tracksIds
            .toMutableList()
            .apply { remove(trackId) }
        val updatedPlaylist = playlist.copy(
            tracksIds = newPlaylistTracksIds,
            tracksCount = playlist.tracksCount - 1
        )
        playlistRepository.updatePlaylist(updatedPlaylist)
        checkOtherPlaylistsContainTrack(trackId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
        playlist.tracksIds.forEach { checkOtherPlaylistsContainTrack(it) }
    }

    override suspend fun updatePlaylist(playlist: Playlist, coverUri: Uri?) {
        val playlistCoverUri = if (playlist.playlistCoverUri != coverUri.toString())
            coverUri?.let { playlistCoverRepository.savePlaylistCover(playlist.id, coverUri)}
        else
            playlist.playlistCoverUri
        playlistRepository.updatePlaylist(playlist.copy(playlistCoverUri = playlistCoverUri))
    }

    private suspend fun checkOtherPlaylistsContainTrack(trackId: String) {
        val playlistsContainTrack = mutableListOf<Playlist>()
            this.playlistRepository.getPlaylists().collect {
                it.filter { playlist ->
                    trackId in playlist.tracksIds
                    playlistsContainTrack.add(playlist)
            }
        }
        if (playlistsContainTrack.isEmpty()) {
            trackRepository.deleteTrack(trackId)
        }
    }
}
