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

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TIMER_DELAY = 500L
    }

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        backButton = binding?.mediaBackButton
        trackImage = binding?.trackImage
        trackName = binding?.trackName
        artistName = binding?.artistName
        trackTime = binding?.trackTime
        trackAlbum = binding?.trackAlbum
        trackYear = binding?.trackYear
        trackGenre = binding?.trackGenre
        trackCountry = binding?.trackCountry
        play = binding?.play
        timer = binding?.timer

        bindTrack(getTrack())

        backButton?.setOnClickListener {
            finish()
        }

        preparePlayer(getTrack())

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
        trackName?.text = track.trackName
        artistName?.text = track.artistName
        trackTime?.text = if (track.trackTime.isNotEmpty()) {
            SimpleDateFormat(
                "mm:ss", Locale.getDefault()).format(track.trackTime.toLong())
        } else {
            ""
        }

        trackAlbum?.text = track.collectionName.orEmpty()
        trackYear?.text = if (!track.releaseDate.isNullOrEmpty() && track.releaseDate != "0") {
            SimpleDateFormat("yyyy").format(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(track.releaseDate) as Date
            ).toString()
        } else {
            ""
        }
        trackGenre?.text = track.primaryGenreName
        trackCountry?.text = track.country

        if (track.artworkUrl100.isNotEmpty()) {
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
        timer?.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(mediaPlayer.currentPosition)
    }
}
