package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.model.TrackRequest
import com.example.playlistmaker.data.search.model.TrackResponse
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.search.TrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun findTracks(expression: String): Flow<Resource<List<Track>>> {

        return flow {
            val response = networkClient.doRequest(TrackRequest(expression))

            when (response.resultCode) {
                200 -> {
                    emit(Resource.Success((response as TrackResponse).results.map {
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
                    }))
                }
                -1 -> {
                    emit(Resource.Error("Проверьте подключение к интернету"))
                }
                else -> {
                    emit(Resource.Error("Ошибка сервера"))
                }
            }
        }
    }
}
