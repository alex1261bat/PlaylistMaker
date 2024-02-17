package com.example.playlistmaker.data.db.impl

import com.example.playlistmaker.data.db.FavoriteTracksRepository
import com.example.playlistmaker.data.db.PlaylistMakerDb
import com.example.playlistmaker.data.db.converters.PlaylistMakerDbConverter
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val playlistMakerDb: PlaylistMakerDb,
    private val playlistMakerDbConverter: PlaylistMakerDbConverter
) : FavoriteTracksRepository {

    override suspend fun addTrackToFavorite(track: Track) {
        playlistMakerDb.trackDao().addTrackToFavorite(
            playlistMakerDbConverter.mapToTrackEntity(track, System.currentTimeMillis()))
    }

    override suspend fun deleteTrackFromFavorite(track: Track) {
        playlistMakerDb.trackDao().deleteTrackFromFavorite(playlistMakerDbConverter.mapToTrackEntity(track))
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> {
        return playlistMakerDb.trackDao().getFavoriteTracks().map {
            it.map { trackEntity -> playlistMakerDbConverter.mapToTrack(trackEntity) }
        }
    }
}
