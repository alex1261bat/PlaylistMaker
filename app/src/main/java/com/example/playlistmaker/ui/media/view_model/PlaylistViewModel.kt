package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.model.Playlist

class PlaylistViewModel : ViewModel() {
    private val playListState = MutableLiveData<List<Playlist>>(listOf())
    val state: LiveData<List<Playlist>> = playListState
}
