package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.search.model.TrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesSearchAPI {

    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String?) : TrackResponse
}
