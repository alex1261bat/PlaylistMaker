package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.models.Track

class FavoriteTracksViewModel : ViewModel() {
    private val favoriteTracksState = MutableLiveData<List<Track>>(listOf())
    val state: LiveData<List<Track>> = favoriteTracksState
}
