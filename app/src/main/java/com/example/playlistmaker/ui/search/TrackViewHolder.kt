package com.example.playlistmaker.ui.search

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackCardBinding
import com.example.playlistmaker.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(
    private val binding: TrackCardBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        binding.ivTrackImage.setImageResource(R.drawable.track_image_placeholder)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.track_image_placeholder)
            .centerCrop()
            .transform(RoundedCorners(ROUNDING_RADIUS))
            .into(binding.ivTrackImage)

        binding.tvTrackTitle.text = track.trackName
        binding.tvTrackOwnerName.text = track.artistName
        binding.tvTrackTime.text = SimpleDateFormat(
            "mm:ss", Locale.getDefault()).format(track.trackTime.toLong())
    }

    companion object {
        internal const val ROUNDING_RADIUS : Int = 8
    }
}
