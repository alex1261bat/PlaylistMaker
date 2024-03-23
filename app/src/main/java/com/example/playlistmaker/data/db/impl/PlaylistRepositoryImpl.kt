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

    override fun getPlaylists(): Flow<List<Playlist>> =
        playlistMakerDb.playlistDao().getPlaylists().map {
        it.map { entity -> playlistMakerDbConverter.mapToPlaylist(entity) }
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        val newPlaylist = playlist.copy(
            tracksIds = playlist.tracksIds + track.trackId,
            tracksCount = playlist.tracksCount + 1
        )

        playlistMakerDb.playlistDao().updatePlaylist(
            playlistMakerDbConverter.mapToPlaylistEntity(newPlaylist))
        playlistMakerDb.playlistTracksDao().addTrackToPlaylist(
            playlistMakerDbConverter.mapToPlaylistTracksEntity(track))
    }

    override fun getPlaylistById(playlistId: String): Flow<Playlist?> {
        return playlistMakerDb.playlistDao().getPlaylistById(playlistId).map {
                entity -> entity?.let { playlistMakerDbConverter.mapToPlaylist(it) } }
    }

    override fun getPlaylistTracks(tracksIds: List<String>): Flow<List<Track>> {
        return playlistMakerDb.playlistTracksDao().getPlaylistTracks(tracksIds).map { entities ->
            entities
                .sortedByDescending { it.createdAt }
                .map { it.let { playlistMakerDbConverter.mapToTrackFromPlaylistTracksEntity(it) } }
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistMakerDb.playlistDao().updatePlaylist(
            playlistMakerDbConverter.mapToPlaylistEntity(playlist)
        )
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistMakerDb.playlistDao().deletePlaylist(
            playlistMakerDbConverter.mapToPlaylistEntity(playlist)
        )
    }
}
