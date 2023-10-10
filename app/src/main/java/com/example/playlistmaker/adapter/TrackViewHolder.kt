package com.example.playlistmaker.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackImage = itemView.findViewById<ImageView>(R.id.track_image)
    private val trackTitle = itemView.findViewById<TextView>(R.id.track_title)
    private val trackOwnerName = itemView.findViewById<TextView>(R.id.track_owner_name)
    private val trackTime = itemView.findViewById<TextView>(R.id.track_time)

    fun bind(track: Track) {
        trackImage.setImageResource(R.drawable.track_image_placeholder)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.track_image_placeholder)
            .centerCrop()
            .transform(RoundedCorners(ROUNDING_RADIUS))
            .into(trackImage)

        trackTitle.text = track.trackName
        trackOwnerName.text = track.artistName
        trackTime.text = SimpleDateFormat(
            "mm:ss", Locale.getDefault()).format(track.trackTime.toLong())
    }

    companion object {
        private const val ROUNDING_RADIUS : Int = 8
    }
}
