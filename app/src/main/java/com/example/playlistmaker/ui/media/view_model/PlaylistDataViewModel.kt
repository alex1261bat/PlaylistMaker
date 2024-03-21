package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.main.BottomNavigationInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.ui.media.PlaylistDataMenuItem
import com.example.playlistmaker.ui.media.PlaylistDataScreenEvent
import com.example.playlistmaker.ui.media.PlaylistDataScreenState
import com.example.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDataViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val bottomNavigationInteractor: BottomNavigationInteractor,
    private val sharingInteractor: SharingInteractor,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private companion object {
        const val KEY_PLAYLIST_ID = "playlist_id"
    }

    private val playlist = MutableStateFlow<Playlist?>(null)
    private val tracks = MutableStateFlow<List<Track>>(listOf())

    private var trackIdForDeletion: String? = null

    private val screenState = MutableLiveData<PlaylistDataScreenState>()
    val playlistDataScreenState: LiveData<PlaylistDataScreenState> = screenState

    private val _menuItems = MutableLiveData<List<PlaylistDataMenuItem>>()
    val menuItems: LiveData<List<PlaylistDataMenuItem>> = _menuItems

    val event = SingleLiveEvent<PlaylistDataScreenEvent>()

    init {
        subscribeOnPlaylist()
        subscribeOnScreenState()
        createMenuItems()
    }

    fun onBackPressed() = navigateBack()

    fun shareButtonClick() {
        if (tracks.value.isNotEmpty()) {
            playlist.value?.let {
                sharingInteractor.sharePlaylist(it.title, it.description ?: "", tracks.value)
            }
        } else
            event.postValue(PlaylistDataScreenEvent.ShowMessageNoTracksInPlaylist)
    }

    fun menuButtonClick() = event.postValue(PlaylistDataScreenEvent.SetMenuVisibility(true))

    fun deletePlaylistConfirm() {
        playlist.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                playlistInteractor.deletePlaylist(it)
                withContext(Dispatchers.Main) {
                    navigateBack()
                }
            }
        }
    }

    fun trackClick(track: Track) {
        playlistInteractor.saveTrackForPlaying(track)
        event.value = PlaylistDataScreenEvent.OpenPlayerScreen
    }

    fun trackLongClick(trackId: String) {
        trackIdForDeletion = trackId
        event.postValue(PlaylistDataScreenEvent.ShowDeleteTrackDialog)
    }

    fun deleteTrackConfirm() {
        playlist.value?.apply {
            trackIdForDeletion?.let { id ->
                viewModelScope.launch(Dispatchers.IO) {
                    playlistInteractor.deleteTrackFromPlaylist(this@apply, id)
                }.invokeOnCompletion { trackIdForDeletion = null }
            }
        }
    }

    fun deleteTrackCancel() {
        trackIdForDeletion = null
    }

    private fun editPlaylistClick() {
        playlist.value?.let { event.postValue(PlaylistDataScreenEvent.NavigateToEditPlaylist(it)) }
    }

    private fun deletePlaylistClick() {
        event.value = PlaylistDataScreenEvent.SetMenuVisibility(false)
        playlist.value?.let {
            event.postValue(PlaylistDataScreenEvent.ShowDeletePlaylistDialog(it.title))
        }
    }

    private fun subscribeOnPlaylist() {
        val playlistId = savedStateHandle.get<String?>(KEY_PLAYLIST_ID)
        playlistId?.let {
            playlistInteractor.getPlaylistById(playlistId).onEach { updatedPlaylist ->
                if (updatedPlaylist.tracksCount != playlist.value?.tracksCount) {
                    getPlaylistTracks(updatedPlaylist.tracksIds)
                }
                playlist.update { updatedPlaylist }
            }.launchIn(viewModelScope)
        }
    }

    private fun getPlaylistTracks(tracksIds: List<String>) {
        playlistInteractor.getPlaylistTracks(tracksIds).onEach { newTracks ->
            tracks.update { newTracks }
        }.launchIn(viewModelScope)
    }

    private fun subscribeOnScreenState() {
        combine(playlist, tracks) { playlist, tracks ->
            val state = PlaylistDataScreenState(
                playlistTitle = playlist?.title ?: "",
                playlistCoverUri = playlist?.playlistCoverUri,
                playlistDescription = playlist?.description ?: "",
                playlistDuration = tracks.calculateTotalDuration(),
                tracksCount = tracks.count(),
                tracks = tracks
            )
            screenState.postValue(state)
        }.launchIn(viewModelScope)
    }

    private fun List<Track>.calculateTotalDuration(): Int {
        val totalDurationInMillis = this.sumOf { it.trackTime.toLong() }
        return SimpleDateFormat("mm", Locale.getDefault()).format(totalDurationInMillis).toInt()
    }

    private fun createMenuItems() {
        _menuItems.value = listOf(
            PlaylistDataMenuItem.Share { shareButtonClick() },
            PlaylistDataMenuItem.Edit { editPlaylistClick() },
            PlaylistDataMenuItem.Delete { deletePlaylistClick() }
        )
    }

    private fun navigateBack() {
        event.postValue(PlaylistDataScreenEvent.NavigateBack)
        bottomNavigationInteractor.setBottomNavigationVisibility(true)
    }
}
