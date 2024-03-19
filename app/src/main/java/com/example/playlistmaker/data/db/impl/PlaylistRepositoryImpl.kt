package com.example.playlistmaker.data.db.impl

import com.example.playlistmaker.data.db.PlaylistMakerDb
import com.example.playlistmaker.data.db.converters.PlaylistMakerDbConverter
import com.example.playlistmaker.data.db.repository.PlaylistRepository
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistMakerDb: PlaylistMakerDb,
    private val playlistMakerDbConverter: PlaylistMakerDbConverter
) : PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        playlistMakerDb.playlistDao()
            .addPlaylist(playlistMakerDbConverter.mapToPlaylistEntity(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist, track: Track) {
        val tracksIds = playlist.tracksIds.toMutableList()
        tracksIds.add(track.trackId)
        val newPlaylist = playlist.copy(tracksIds = tracksIds, tracksCount = playlist.tracksCount + 1)

        playlistMakerDb.playlistDao().updatePlaylist(
            playlistMakerDbConverter.mapToPlaylistEntity(newPlaylist))
        playlistMakerDb.playlistTracksDao().addTrackToPlaylist(
            playlistMakerDbConverter.mapToPlaylistTracksEntity(track))
    }

    override fun getPlaylists(): Flow<List<Playlist>> =
        playlistMakerDb.playlistDao().getPlaylists().map {
        it.map { entity -> playlistMakerDbConverter.mapToPlaylist(entity) }
    }
}
