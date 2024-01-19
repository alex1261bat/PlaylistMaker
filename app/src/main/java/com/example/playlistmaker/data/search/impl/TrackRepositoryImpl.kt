package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.model.TrackRequest
import com.example.playlistmaker.data.search.model.TrackResponse
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.search.TrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun findTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackRequest(expression))

        return when (response.resultCode) {
            200 -> {
                Resource.Success((response as TrackResponse).results.map {
                    Track(it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTime,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl)
                })
            }
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }
}
