package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.data.search.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.player.PlayerInteractor

class PlayerInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository) : PlayerInteractor {

    override fun getTrackForPlaying(): Track? {
        return searchHistoryRepository.getTrackForPlaying()
    }
}
