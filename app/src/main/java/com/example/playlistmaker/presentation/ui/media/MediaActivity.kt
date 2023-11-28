package com.example.playlistmaker.presentation.ui.media

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.example.playlistmaker.domain.models.MediaPlayerStatus
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.search.SearchActivity.Companion.TRACK_DATA
import com.example.playlistmaker.presentation.ui.search.TrackViewHolder
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MediaActivity : AppCompatActivity() {
    private var binding: ActivityMediaBinding? = null
    private val handler = Handler(Looper.getMainLooper())
    private var mediaPlayer = MediaPlayer()
    private lateinit var playerState: MediaPlayerStatus

    companion object {
        private const val TIMER_DELAY = 500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        bindTrack(getTrack())

        binding?.mediaBackButton?.setOnClickListener {
            finish()
        }

        preparePlayer(getTrack())
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(updateTimer())
    }

    private fun getTrack(): Track {
        val gson = Gson()
        val trackData = intent.extras?.getString(TRACK_DATA)

        val track = if (trackData.isNullOrEmpty()) {
            Track("", "", "", "", "",
                "", "", "", "", "")
        } else {
            gson.fromJson(trackData, Track::class.java)
        }

        return track
    }

    private fun bindTrack(track: Track) {
        binding?.trackName?.text = track.trackName
        binding?.artistName?.text = track.artistName
        binding?.trackTime?.text = if (track.trackTime.isNotEmpty()) {
            SimpleDateFormat(
                "mm:ss", Locale.getDefault()).format(track.trackTime.toLong())
        } else {
            ""
        }

        binding?.trackAlbum?.text = track.collectionName.orEmpty()
        binding?.trackYear?.text = if (!track.releaseDate.isNullOrEmpty() && track.releaseDate != "0") {
            SimpleDateFormat("yyyy").format(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(track.releaseDate) as Date
            ).toString()
        } else {
            ""
        }

        binding?.trackGenre?.text = track.primaryGenreName
        binding?.trackCountry?.text = track.country

        if (track.artworkUrl100.isNotEmpty()) {
            binding?.trackImage?.setImageResource(R.drawable.track_image_placeholder)

            Glide.with(binding?.trackImage!!)
                .load(track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
                .placeholder(R.drawable.track_image_placeholder)
                .centerCrop()
                .transform(RoundedCorners(TrackViewHolder.ROUNDING_RADIUS))
                .into(binding?.trackImage!!)
        }
    }

    private fun preparePlayer(track: Track) {
        if (!track.previewUrl.isNullOrEmpty()) {
            mediaPlayer.setDataSource(track.previewUrl)
            mediaPlayer.prepareAsync()

            binding?.play?.setOnClickListener { playbackControl() }

            mediaPlayer.setOnPreparedListener {
                binding?.play?.isEnabled = true
                playerState = MediaPlayerStatus.STATE_PREPARED
                setTimerValue()
            }

            mediaPlayer.setOnCompletionListener {
                binding?.play?.setImageResource(R.drawable.button_play)
                playerState = MediaPlayerStatus.STATE_PREPARED
                handler.removeCallbacks(updateTimer())
                binding?.timer?.text = getString(R.string.timer_start_value_00_00)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding?.play?.setImageResource(R.drawable.button_pause)
        playerState = MediaPlayerStatus.STATE_PLAYING
        handler.post(updateTimer())
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding?.play?.setImageResource(R.drawable.button_play)
        playerState = MediaPlayerStatus.STATE_PAUSED
        handler.removeCallbacks(updateTimer())
    }

    private fun playbackControl() {
        when(playerState) {
            MediaPlayerStatus.STATE_PLAYING -> {
                pausePlayer()
            }
            MediaPlayerStatus.STATE_PREPARED, MediaPlayerStatus.STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun updateTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == MediaPlayerStatus.STATE_PLAYING) {
                    setTimerValue()
                    handler.postDelayed(this, TIMER_DELAY)
                }
            }
        }
    }

    private fun setTimerValue() {
        binding?.timer?.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(mediaPlayer.currentPosition)
    }
}
