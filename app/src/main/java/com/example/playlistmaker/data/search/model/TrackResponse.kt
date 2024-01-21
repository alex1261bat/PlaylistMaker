package com.example.playlistmaker.data.search.model

import com.example.playlistmaker.data.network.Response

data class TrackResponse(val results: List<TrackDto>) : Response()
