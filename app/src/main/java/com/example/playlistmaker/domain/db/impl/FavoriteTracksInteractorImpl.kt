package com.example.playlistmaker.domain.db.impl

import com.example.playlistmaker.data.db.repository.FavoriteTracksRepository
import com.example.playlistmaker.data.search.SearchHistoryRepository
import com.example.playlistmaker.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val favoriteTracksRepository: FavoriteTracksRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : FavoriteTracksInteractor {
    override suspend fun addTrackToFavorite(track: Track) {
        favoriteTracksRepository.addTrackToFavorite(track)
    }

    override suspend fun deleteTrackFromFavorite(track: Track) {
        favoriteTracksRepository.deleteTrackFromFavorite(track)
    }

    override suspend fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksRepository.getFavoriteTracks()
    }

    override fun saveTrackForPlaying(track: Track) {
        searchHistoryRepository.saveTrackForPlaying(track)
    }
}
