package com.example.playlistmaker.model

import android.content.SharedPreferences
import com.example.playlistmaker.activity.SEARCH_HISTORY
import com.google.gson.Gson

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    private val gson = Gson()
    val trackHistoryList = mutableListOf<Track>()

    fun getHistoryList(): List<Track> {
        trackHistoryList.clear()

        if (sharedPreferences.getString(SEARCH_HISTORY, "")?.isNotEmpty() == true) {
            trackHistoryList.addAll(gson.fromJson(
                sharedPreferences.getString(SEARCH_HISTORY, ""),
                Array<Track>::class.java
            ).toList())
        }
        
        return trackHistoryList
    }

    fun addTrackToHistory(track: Track) {
        if (trackHistoryList.size < 10) {
            checkTrack(track)
            saveHistoryPrefs()
        } else {
            trackHistoryList.removeLast()
            checkTrack(track)
            saveHistoryPrefs()
        }
    }

    fun clearHistory() {
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
