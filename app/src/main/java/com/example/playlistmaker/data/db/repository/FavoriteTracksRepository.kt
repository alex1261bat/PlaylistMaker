package com.example.playlistmaker.data.db.repository

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend  fun addTrackToFavorite(track: Track)
    suspend  fun deleteTrackFromFavorite(track: Track)
    suspend  fun getFavoriteTracks(): Flow<List<Track>>
}
