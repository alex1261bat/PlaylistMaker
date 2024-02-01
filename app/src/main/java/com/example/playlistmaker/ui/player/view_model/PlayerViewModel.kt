package com.example.playlistmaker.ui.player.view_model

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.model.MediaPlayerStatus
import com.example.playlistmaker.ui.player.PlayerScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    application: Application,
    playerInteractor: PlayerInteractor,
    private val mediaPlayer: MediaPlayer
) : AndroidViewModel(application) {
    private val track = playerInteractor.getTrackForPlaying()
    private var trackTimerJob: Job? = null
    private val mediaPlayerState = MutableLiveData<PlayerScreenState>()
    val state: LiveData<PlayerScreenState> = mediaPlayerState

    companion object {
        private const val TIMER_DELAY_MILLIS = 500L
    }

    init {
        mediaPlayerState.value = PlayerScreenState(MediaPlayerStatus.STATE_PAUSED, track)
        preparePlayer()
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
}
