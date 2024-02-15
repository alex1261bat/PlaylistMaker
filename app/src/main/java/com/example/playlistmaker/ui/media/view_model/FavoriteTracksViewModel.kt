package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.media.FavoriteTracksScreenEvent
import com.example.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {
    private val favoriteTracksState = MutableLiveData<List<Track>>(listOf())
    val state: LiveData<List<Track>> = favoriteTracksState
    val event = SingleLiveEvent<FavoriteTracksScreenEvent>()

    init {
        subscribeOnFavoriteTracks()
    }

    fun clickTrack(track: Track) {
        favoriteTracksInteractor.saveTrackForPlaying(track)
        event.value = FavoriteTracksScreenEvent.OpenPlayerScreen
    }

    private fun subscribeOnFavoriteTracks() {
        viewModelScope.launch(Dispatchers.IO) {
            favoriteTracksInteractor.getFavoriteTracks().collect {
                favoriteTracksState.postValue(it)
            }
        }
    }
}
