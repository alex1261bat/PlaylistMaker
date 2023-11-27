package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository) : SearchHistoryInteractor {

    override fun getHistoryList(): List<Track> {
        return searchHistoryRepository.getHistoryList()
    }

    override fun addTrackToHistory(track: Track) {
        searchHistoryRepository.addTrackToHistory(track)
    }

    override fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }
}