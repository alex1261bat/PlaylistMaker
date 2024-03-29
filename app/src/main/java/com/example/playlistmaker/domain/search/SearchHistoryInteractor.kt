package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.model.Track

interface SearchHistoryInteractor {
    fun getHistoryList(): List<Track>

    fun addTrackToHistory(track: Track)

    fun clearHistory()

    fun saveTrackForPlaying(track: Track)
}
