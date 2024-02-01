package com.example.playlistmaker.data.search

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun findTracks(expression: String): Flow<Resource<List<Track>>>
}
