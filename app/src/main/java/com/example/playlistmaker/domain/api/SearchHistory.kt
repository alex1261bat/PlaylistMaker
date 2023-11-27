package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistory {
    fun getHistoryList(): List<Track>

    fun addTrackToHistory(track: Track)

    fun clearHistory()
}
