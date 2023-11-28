package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TrackRequest
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun findTracks(expression: String): List<Track>? {
        val response = networkClient.doRequest(TrackRequest(expression))
        return when (response.resultCode) {
            200 -> {
                (response as TrackResponse).results.map {
                    Track(it.trackId, it.trackName, it.artistName, it.trackTime, it.artworkUrl100,
                        it.collectionName, it.releaseDate, it.primaryGenreName, it.country,
                        it.previewUrl)
                }
            }
            404 -> {
                emptyList()
            }
            else -> {
                null
            }
        }
    }
}
