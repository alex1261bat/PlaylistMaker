package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.db.PlaylistMakerDb
import com.example.playlistmaker.data.search.model.TrackRequest
import com.example.playlistmaker.data.search.model.TrackResponse
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.search.TrackRepository
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val playlistMakerDb: PlaylistMakerDb) : TrackRepository {
    override fun findTracks(expression: String): Flow<Resource<List<Track>>> {

        return flow {
            val response = networkClient.doRequest(TrackRequest(expression))

            when (response.resultCode) {
                200 -> {
                    val favoriteTracksIds = playlistMakerDb.favoriteTracksDao().getFavoriteTracksIds()
                    val tracksList = (response as TrackResponse).results.map {
                        Track(
                            it.trackId,
                            it.trackName,
                            it.artistName,
                            it.trackTime,
                            it.artworkUrl100,
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl,
                            isFavorite = favoriteTracksIds.contains(it.trackId)
                        )
                    }

                    emit(Resource.Success(tracksList))
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
