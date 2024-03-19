package com.example.playlistmaker.ui.player.view_model

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.model.MediaPlayerStatus
import com.example.playlistmaker.ui.player.PlayerScreenEvent
import com.example.playlistmaker.ui.player.PlayerScreenState
import com.example.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    application: Application,
    playerInteractor: PlayerInteractor,
    private val mediaPlayer: MediaPlayer,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : AndroidViewModel(application) {
    private var track = playerInteractor.getTrackForPlaying()
    private var trackTimerJob: Job? = null
    private val mediaPlayerState = MutableLiveData<PlayerScreenState>()
    val state: LiveData<PlayerScreenState> = mediaPlayerState
    private val mediaPlayerPlaylists = MutableLiveData<List<Playlist>>(listOf())
    val playlists: LiveData<List<Playlist>> = mediaPlayerPlaylists
    val event = SingleLiveEvent<PlayerScreenEvent>()

    companion object {
        private const val TIMER_DELAY_MILLIS = 500L
    }

    init {
        mediaPlayerState.value = PlayerScreenState(MediaPlayerStatus.STATE_PAUSED, track)
        subscribeOnFavoriteTracks()
        preparePlayer()
        subscribeOnPlaylists()
    }

    override fun onCleared() {
        mediaPlayer.release()
        super.onCleared()
    }

    fun onPause() {
        pausePlayer()
    }

    fun onStop() {
        if (getCurrentScreenState().playerState != MediaPlayerStatus.STATE_PAUSED) {
            trackTimerJob?.cancel()
            mediaPlayer.release()
        } else {
            startPlayer()
        }
    }

    fun clickPlay() {
        track?.let {
            when (getCurrentScreenState().playerState) {
                MediaPlayerStatus.STATE_PLAYING -> pausePlayer()
                MediaPlayerStatus.STATE_PREPARED, MediaPlayerStatus.STATE_PAUSED -> startPlayer()
            }
        }
    }

    fun likeButtonClick() {
        viewModelScope.launch {
            val track = track ?: return@launch

            if (track.isFavorite) {
                favoriteTracksInteractor.deleteTrackFromFavorite(track)
            } else {
                favoriteTracksInteractor.addTrackToFavorite(track)
            }
        }
    }

    fun addButtonClick() {
        event.value = PlayerScreenEvent.OpenPlaylistsBottomSheet
    }

    fun createPlaylistButtonClick() {
        event.postValue(PlayerScreenEvent.NavigateToCreatePlaylistFragment)
    }

    fun playlistClick(playlist: Playlist) {
        track?.let {
            if (it.trackId !in playlist.tracksIds.toSet()) {
                viewModelScope.launch(Dispatchers.IO) {
                    playlistInteractor.updatePlaylist(playlist, it)
                    withContext(Dispatchers.Main) {
                        event.value = PlayerScreenEvent.ClosePlaylistsBottomSheet
                        event.value = PlayerScreenEvent.ShowAddedTrackMessage(playlist.title)
                    }
                }
            } else {
                event.value = PlayerScreenEvent.ShowTrackAlreadyInPlaylistMessage(playlist.title)
            }
        }
    }

    private fun subscribeOnPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.getPlaylists().collect { mediaPlayerPlaylists.postValue(it) }
        }
    }

    private fun preparePlayer() {
        if (!track?.previewUrl.isNullOrEmpty()) {
            mediaPlayer.apply {
                setDataSource(getApplication(), Uri.parse(track?.previewUrl))
                prepareAsync()

                setOnPreparedListener {
                    mediaPlayerState.value = getCurrentScreenState()
                        .copy(playerState = MediaPlayerStatus.STATE_PREPARED)
                }

                setOnCompletionListener {
                    mediaPlayerState.value =
                        getCurrentScreenState().copy(
                            playerState = MediaPlayerStatus.STATE_PREPARED,
                            trackTime = ""
                        )
                }
            }
        }
    }

    private fun startPlayer() {
        if (getCurrentScreenState().playerState != MediaPlayerStatus.STATE_PLAYING) {
            mediaPlayer.start()
            trackTimerJob?.cancel()
            trackTimerJob = viewModelScope.launch {
                while (mediaPlayer.isPlaying){
                    val time = SimpleDateFormat("mm:ss", Locale.getDefault())
                        .format(mediaPlayer.currentPosition)

                    mediaPlayerState.postValue(
                        PlayerScreenState(MediaPlayerStatus.STATE_PLAYING, track, time))
                    delay(TIMER_DELAY_MILLIS)
                }
            }
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        mediaPlayerState.value = getCurrentScreenState()
            .copy(playerState = MediaPlayerStatus.STATE_PAUSED)
    }

    private fun getCurrentScreenState() = mediaPlayerState.value ?: PlayerScreenState()

    private fun subscribeOnFavoriteTracks() {
        viewModelScope.launch(Dispatchers.IO) {
            favoriteTracksInteractor.getFavoriteTracks().collect { favoriteTracks ->
                track?.let {
                    track = it.copy(isFavorite = it.trackId in favoriteTracks.map { track ->
                        track.trackId
                    }.toSet())
                    mediaPlayerState.postValue(
                        getCurrentScreenState().copy(isFavorite = track?.isFavorite ?: false)
                    )
                }
            }
        }
    }
}
