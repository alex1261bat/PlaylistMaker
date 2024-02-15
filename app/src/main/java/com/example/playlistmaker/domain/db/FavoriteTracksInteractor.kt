package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    suspend fun addTrackToFavorite(track: Track)
    suspend fun deleteTrackFromFavorite(track: Track)
    suspend fun getFavoriteTracks(): Flow<List<Track>>
    fun saveTrackForPlaying(track: Track)
}
