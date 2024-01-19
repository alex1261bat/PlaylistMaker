package com.example.playlistmaker.ui.search

import com.example.playlistmaker.domain.models.Track

data class SearchScreenState (
    val trackList: List<Track> = listOf(),
    val searchHistoryList: List<Track> = listOf(),
    val clearIconVisibility: Boolean = false,
    val clearHistoryButtonVisibility: Boolean = false,
    val youSearchedVisibility: Boolean = false,
    val connectionErrorVisibility: Boolean = false,
    val nothingFoundVisibility: Boolean = false,
    val progressBarVisibility: Boolean = false,
    val historyVisibility: Boolean = true
)
