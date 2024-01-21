package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.data.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun getHistoryList(): List<Track> {
        return searchHistoryRepository.getHistoryList()
    }

    override fun addTrackToHistory(track: Track) {
        searchHistoryRepository.addTrackToHistory(track)
    }

    override fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }

    override fun saveTrackForPlaying(track: Track) {
        searchHistoryRepository.saveTrackForPlaying(track)
    }
}