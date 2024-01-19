package com.example.playlistmaker.ui.player.view_model

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.player.model.MediaPlayerStatus
import com.example.playlistmaker.ui.player.PlayerScreenState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val playerInteractor = Creator.providePlayerInteractor(
        Creator.provideSharedPreferences(application))
    private val track = playerInteractor.getTrackForPlaying()
    private val trackTimerRunnable = object : Runnable {
            override fun run() {
                val time = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(mediaPlayer.currentPosition)

                if (getCurrentScreenState().playerState == MediaPlayerStatus.STATE_PLAYING) {
                    handler.postDelayed(this, TIMER_DELAY)
                    mediaPlayerState.postValue(
                        PlayerScreenState(MediaPlayerStatus.STATE_PLAYING, track, time))
                } else {
                    pausePlayer()
                }
            }
        }
    private val mediaPlayerState = MutableLiveData<PlayerScreenState>()
    val state: LiveData<PlayerScreenState> = mediaPlayerState


    companion object {
        private const val TIMER_DELAY = 500L
    }

    init {
        mediaPlayerState.value = PlayerScreenState(MediaPlayerStatus.STATE_PAUSED, track)
        preparePlayer()
    }


    override fun onCleared() {
        handler.removeCallbacks(trackTimerRunnable)
        mediaPlayer.release()
        super.onCleared()
    }

    fun onPause() {
        pausePlayer()
    }

    fun onStop() {
        if (getCurrentScreenState().playerState != MediaPlayerStatus.STATE_PAUSED) {
            handler.removeCallbacks(trackTimerRunnable)
            mediaPlayer.release()
        }
    }

    fun clickPlay() {
        track?.let {
            when (getCurrentScreenState().playerState) {
                MediaPlayerStatus.STATE_PLAYING -> pausePlayer()
                MediaPlayerStatus.STATE_PREPARED, MediaPlayerStatus.STATE_PAUSED -> startPlayer()
            }

            handler.post(trackTimerRunnable)
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
            mediaPlayerState.value = getCurrentScreenState()
                .copy(playerState = MediaPlayerStatus.STATE_PLAYING)
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        mediaPlayerState.value = getCurrentScreenState()
            .copy(playerState = MediaPlayerStatus.STATE_PAUSED)
    }

    private fun getCurrentScreenState() = mediaPlayerState.value ?: PlayerScreenState()
}
