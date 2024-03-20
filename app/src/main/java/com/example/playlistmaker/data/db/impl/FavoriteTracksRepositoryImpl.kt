package com.example.playlistmaker.data.db.impl

import com.example.playlistmaker.data.db.repository.FavoriteTracksRepository
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
        playlistMakerDb.favoriteTracksDao().addTrackToFavorite(
            playlistMakerDbConverter.mapToFavoriteTracksEntity(track, System.currentTimeMillis()))
    }

    override suspend fun deleteTrackFromFavorite(track: Track) {
        playlistMakerDb.favoriteTracksDao().deleteTrackFromFavorite(playlistMakerDbConverter.mapToFavoriteTracksEntity(track))
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> {
        return playlistMakerDb.favoriteTracksDao().getFavoriteTracks().map {
            it.map { trackEntity -> playlistMakerDbConverter.mapToTrack(trackEntity) }
        }
    }
}
