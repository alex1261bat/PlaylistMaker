package com.example.playlistmaker.activity

import android.media.MediaPlayer
import androidx.appcompat.app .AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.activity.SearchActivity.Companion.TRACK_DATA
import com.example.playlistmaker.adapter.TrackViewHolder
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.example.playlistmaker.model.MediaPlayerStatus
import com.example.playlistmaker.model.MediaPlayerStatus.*
import com.example.playlistmaker.model.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MediaActivity : AppCompatActivity() {
    private var binding: ActivityMediaBinding? = null
    private var backButton: Toolbar? = null
    private var trackImage: ImageView? = null
    private var trackName: TextView? = null
    private var artistName: TextView? = null
    private var trackTime: TextView? = null
    private var trackAlbum: TextView? = null
    private var trackYear: TextView? = null
    private var trackGenre: TextView? = null
    private var trackCountry: TextView? = null
    private var play: ImageButton? = null
    private var timer: TextView? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var playerState: MediaPlayerStatus
    private var mediaPlayer = MediaPlayer()

    companion object {
        private const val TIMER_DELAY = 500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        bindTrack(getTrack())

        backButton = binding?.mediaBackButton

        backButton?.setOnClickListener {
            finish()
        }

        preparePlayer(getTrack())

        play = binding?.play

        play?.setOnClickListener {
            playbackControl()
        }
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
        trackName = binding?.trackName
        trackName?.text = track.trackName
        artistName = binding?.artistName
        artistName?.text = track.artistName
        trackTime = binding?.trackTime
        trackTime?.text = if (track.trackTime.isNotEmpty()) {
            SimpleDateFormat(
                "mm:ss", Locale.getDefault()).format(track.trackTime.toLong())
        } else {
            ""
        }

        trackAlbum = binding?.trackAlbum
        trackAlbum?.text = track.collectionName.orEmpty()
        trackYear = binding?.trackYear
        trackYear?.text = if (!track.releaseDate.isNullOrEmpty() && track.releaseDate != "0") {
            SimpleDateFormat("yyyy").format(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(track.releaseDate) as Date
            ).toString()
        } else {
            ""
        }

        trackGenre = binding?.trackGenre
        trackGenre?.text = track.primaryGenreName
        trackCountry = binding?.trackCountry
        trackCountry?.text = track.country

        if (track.artworkUrl100.isNotEmpty()) {
            trackImage = binding?.trackImage
            trackImage?.setImageResource(R.drawable.track_image_placeholder)

            Glide.with(trackImage!!)
                .load(track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
                .placeholder(R.drawable.track_image_placeholder)
                .centerCrop()
                .transform(RoundedCorners(TrackViewHolder.ROUNDING_RADIUS))
                .into(trackImage!!)
        }
    }

    private fun preparePlayer(track: Track) {
        if (!track.previewUrl.isNullOrEmpty()) {
            mediaPlayer.setDataSource(track.previewUrl)
            mediaPlayer.prepareAsync()

            mediaPlayer.setOnPreparedListener {
                play?.isEnabled = true
                playerState = STATE_PREPARED
                setTimerValue()
            }

            mediaPlayer.setOnCompletionListener {
                play?.setImageResource(R.drawable.button_play)
                playerState = STATE_PREPARED
                handler.removeCallbacks(updateTimer())
                timer?.text = getString(R.string.timer_start_value_00_00)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        play?.setImageResource(R.drawable.button_pause)
        playerState = STATE_PLAYING
        handler.post(updateTimer())
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play?.setImageResource(R.drawable.button_play)
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateTimer())
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun updateTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    setTimerValue()
                    handler.postDelayed(this, TIMER_DELAY)
                }
            }
        }
    }

    private fun setTimerValue() {
        timer = binding?.timer
        timer?.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(mediaPlayer.currentPosition)
    }
}
