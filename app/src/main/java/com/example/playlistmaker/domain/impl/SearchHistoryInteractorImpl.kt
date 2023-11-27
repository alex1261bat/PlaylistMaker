package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistory
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(
    private val searchHistory: SearchHistory) : SearchHistoryInteractor {

    override fun getHistoryList(): List<Track> {
        return searchHistory.getHistoryList()
    }

    override fun addTrackToHistory(track: Track) {
        searchHistory.addTrackToHistory(track)
    }

    override fun clearHistory() {
        searchHistory.clearHistory()
    }
}