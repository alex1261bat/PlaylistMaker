package com.example.playlistmaker.ui.media.view_model

import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.main.BottomNavigationInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.ui.media.NewPlaylistEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    bottomNavigationInteractor: BottomNavigationInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val savedStateHandle: SavedStateHandle
) : NewPlaylistViewModel(
    bottomNavigationInteractor,
    playlistInteractor
) {

    companion object {
        private const val KEY_PLAYLIST = "playlist"
    }

    private val playlist = MutableLiveData<Playlist>()
    val editPlaylist: LiveData<Playlist> = playlist

    init {
        setPlaylistData()
    }

    override fun completeButtonClick() {
        editPlaylist.value?.let {
            val updatedPlaylist = it.copy(title = playlistTitle, description = playlistDescription)
            viewModelScope.launch(Dispatchers.IO) {
                playlistInteractor.updatePlaylist(updatedPlaylist, playlistCoverUri.value)
                playlistEvent.postValue(NewPlaylistEvent.NavigateBack)
            }
        }
    }

    override fun onBackPressed() = playlistEvent.postValue(NewPlaylistEvent.NavigateBack)

    private fun setPlaylistData() {
        savedStateHandle.get<Playlist>(KEY_PLAYLIST)?.let {
            playlistCoverUri.value = it.playlistCoverUri?.toUri()
            playlist.value = it
        }
    }
}
