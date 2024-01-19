package com.example.playlistmaker.data.search

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource

interface TrackRepository {
    fun findTracks(expression: String): Resource<List<Track>>
}
