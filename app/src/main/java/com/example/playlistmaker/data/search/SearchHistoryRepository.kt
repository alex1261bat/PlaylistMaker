package com.example.playlistmaker.data.search

import com.example.playlistmaker.domain.model.Track

interface SearchHistoryRepository {
    fun getHistoryList(): List<Track>

    fun addTrackToHistory(track: Track)

    fun clearHistory()

    fun getTrackForPlaying(): Track?

    fun saveTrackForPlaying(track: Track?)
}
