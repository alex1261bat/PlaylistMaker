package com.example.playlistmaker.activity

import androidx.appcompat.app .AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.adapter.TrackViewHolder
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.example.playlistmaker.model.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MediaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaBinding
    private lateinit var backButton: Toolbar
    private lateinit var trackImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backButton = binding.mediaBackButton
        trackImage = binding.trackImage
        trackName = binding.trackName
        artistName = binding.artistName
        trackTime = binding.trackTime
        trackAlbum = binding.trackAlbum
        trackYear = binding.trackYear
        trackGenre = binding.trackGenre
        trackCountry = binding.trackCountry

        bindTrack(getTrack())

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun getTrack(): Track {
        val gson = Gson()
        val trackData = intent.extras?.getString("trackData")

        val track = if (trackData.isNullOrEmpty()) {
            Track("", "", "", "", "",
                "", "", "", "")
        } else {
            gson.fromJson(trackData, Track::class.java)
        }

        return track
    }

    private fun bindTrack(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = if (track.trackTime.isNotEmpty()) {
            SimpleDateFormat(
                "mm:ss", Locale.getDefault()).format(track.trackTime.toLong())
        } else {
            ""
        }

        trackAlbum.text = track.collectionName.orEmpty()
        trackYear.text = if (track.releaseDate.isNotEmpty() && track.releaseDate != "0") {
            SimpleDateFormat("yyyy").format(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(track.releaseDate) as Date
            ).toString()
        } else {
            ""
        }
        trackGenre.text = track.primaryGenreName
        trackCountry.text = track.country

        if (track.artworkUrl100.isNotEmpty()) {
            trackImage.setImageResource(R.drawable.track_image_placeholder)

            Glide.with(trackImage)
                .load(track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
                .placeholder(R.drawable.track_image_placeholder)
                .centerCrop()
                .transform(RoundedCorners(TrackViewHolder.ROUNDING_RADIUS))
                .into(trackImage)
        }
    }
}
