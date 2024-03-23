package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.main.BottomNavigationInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.ui.media.PlaylistScreenEvent
import com.example.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val bottomNavigationInteractor: BottomNavigationInteractor
) : ViewModel() {
    private val playListState = MutableLiveData<List<Playlist>>(listOf())
    val state: LiveData<List<Playlist>> = playListState
    val playlistEvent = SingleLiveEvent<PlaylistScreenEvent>()

    init {
        subscribeOnPlaylists()
    }

    fun newPlaylistButtonClick() {
        bottomNavigationInteractor.setBottomNavigationVisibility(false)
        playlistEvent.value = PlaylistScreenEvent.NavigateToNewPlaylist
    }

    fun playlistClicked(playlistId: String) {
        bottomNavigationInteractor.setBottomNavigationVisibility(false)
        playlistEvent.postValue(PlaylistScreenEvent.NavigateToPlaylistData(playlistId))
    }

    private fun subscribeOnPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.getPlaylists().collect { playListState.postValue(it) }
        }
    }
}
