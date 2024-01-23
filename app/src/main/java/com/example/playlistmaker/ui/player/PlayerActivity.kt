package com.example.playlistmaker.ui.player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.player.model.MediaPlayerStatus
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.TrackViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private var binding: ActivityPlayerBinding? = null
    private val viewModel: PlayerViewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initViews()
        initObservers()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    private fun initViews(){
        binding?.apply {
            tbPlayerBack.setNavigationOnClickListener { finish() }
            ibPlay.setOnClickListener { viewModel.clickPlay() }
        }
    }

    private fun initObservers() {
        viewModel.state.observe(this) {
            it.track?.let { track -> bindTrack(track) }
            if (it.track != null) {
                binding?.tvTimer?.text = it.trackTime.ifEmpty { getString(R.string.timer_start_value_00_00) }
            }

            it?.playerState?.let { state ->
                when (state) {
                    MediaPlayerStatus.STATE_PLAYING ->
                        binding?.ibPlay?.setImageResource(R.drawable.button_pause)

                    MediaPlayerStatus.STATE_PREPARED,
                    MediaPlayerStatus.STATE_PAUSED ->
                        binding?.ibPlay?.setImageResource(R.drawable.button_play)
                }
            }
        }
    }

    private fun bindTrack(track: Track) {
        binding?.tvTrackName?.text = track.trackName
        binding?.tvArtistName?.text = track.artistName
        binding?.tvTrackTime?.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTime.toLong()).orEmpty()

        binding?.tvTrackAlbum?.text = track.collectionName.orEmpty()
        binding?.tvTrackYear?.text = track.releaseDate?.substring(0, 4).orEmpty()

        binding?.tvTrackGenre?.text = track.primaryGenreName
        binding?.tvTrackCountry?.text = track.country

        if (track.artworkUrl100.isNotEmpty()) {
            binding?.ivTrackImage?.setImageResource(R.drawable.track_image_placeholder)

            Glide.with(binding?.ivTrackImage!!)
                .load(track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
                .placeholder(R.drawable.track_image_placeholder)
                .centerCrop()
                .transform(RoundedCorners(TrackViewHolder.ROUNDING_RADIUS))
                .into(binding?.ivTrackImage!!)
        }
    }
}
