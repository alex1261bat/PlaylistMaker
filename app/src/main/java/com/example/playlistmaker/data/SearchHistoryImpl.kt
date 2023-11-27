package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SearchHistory
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.main.SEARCH_HISTORY
import com.google.gson.Gson

class SearchHistoryImpl(private val sharedPreferences: SharedPreferences) : SearchHistory {
    private val gson = Gson()
    private val trackHistoryList = mutableListOf<Track>()

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
