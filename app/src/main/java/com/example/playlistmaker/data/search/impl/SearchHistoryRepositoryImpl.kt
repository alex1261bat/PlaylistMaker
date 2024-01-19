package com.example.playlistmaker.data.search.impl

import android.content.SharedPreferences
import com.example.playlistmaker.data.search.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SearchHistoryRepository {
    private val gson = Gson()
    private val trackHistoryList = mutableListOf<Track>()
    private var trackForPlaying: Track? = null

    companion object {
        const val SEARCH_HISTORY = "search_history"
        const val TRACK_FOR_PLAYING = "track_for_playing"
    }

    override fun getHistoryList(): List<Track> {
        trackHistoryList.clear()

        if (sharedPreferences.getString(SEARCH_HISTORY, "")?.isNotEmpty() == true) {
            trackHistoryList.addAll(gson.fromJson(
                sharedPreferences.getString(SEARCH_HISTORY, ""),
                Array<Track>::class.java
            ).toList())
        }
        
        return trackHistoryList
    }

    override fun addTrackToHistory(track: Track) {
        if (trackHistoryList.size < 10) {
            checkTrack(track)
            saveHistoryPrefs()
        } else {
            trackHistoryList.removeLast()
            checkTrack(track)
            saveHistoryPrefs()
        }
    }

    override fun clearHistory() {
        trackHistoryList.clear()
        sharedPreferences.edit()
            .remove(SEARCH_HISTORY)
            .apply()
    }

    override fun getTrackForPlaying(): Track? {
        if (sharedPreferences.getString(TRACK_FOR_PLAYING, "")?.isNotEmpty() == true) {
            trackForPlaying = gson.fromJson(
                sharedPreferences.getString(TRACK_FOR_PLAYING, ""), Track :: class.java)
        }

        sharedPreferences.edit()
            .remove(TRACK_FOR_PLAYING)
            .apply()

        return trackForPlaying
    }

    override fun saveTrackForPlaying(track: Track?) {
        sharedPreferences.edit()
            .putString(TRACK_FOR_PLAYING, gson.toJson(track))
            .apply()
    }

    private fun checkTrack(track: Track) {
        if (trackHistoryList.contains(track)) {
            trackHistoryList.remove(track)
        }

        trackHistoryList.add(0, track)
    }

    private fun saveHistoryPrefs() {
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY, gson.toJson(trackHistoryList))
            .apply()
    }
}
