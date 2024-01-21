package com.example.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackCardBinding
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(private val clickTrack: (track: Track) -> Unit
): RecyclerView.Adapter<TrackViewHolder> () {
    private var trackList = emptyList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val item = trackList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { clickTrack(item) }
    }

    override fun getItemCount(): Int = trackList.size

    fun updateData(newListTrack: List<Track>) {
        trackList = newListTrack
        notifyDataSetChanged()
    }
}
