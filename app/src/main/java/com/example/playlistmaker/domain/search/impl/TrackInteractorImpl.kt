package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.data.search.TrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

class TrackInteractorImpl(private val repository: TrackRepository)  : TrackInteractor {

    override fun findTracks(expression: String): Flow<Resource<List<Track>>> {
        return repository.findTracks(expression)
    }
}
