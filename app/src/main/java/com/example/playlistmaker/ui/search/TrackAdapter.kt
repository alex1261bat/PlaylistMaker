package com.example.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackCardBinding
import com.example.playlistmaker.domain.model.Track

class TrackAdapter(
    private val clickTrack: (track: Track) -> Unit,
    private val longClickTrack: ((trackId: String) -> Unit)? = null
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
        holder.itemView.apply {
            setOnClickListener { clickTrack(item) }
            setOnLongClickListener {
                longClickTrack?.let { onLongClick -> onLongClick(item.trackId) }
                true
            }
        }
    }

    override fun getItemCount(): Int = trackList.size

    fun updateData(newListTrack: List<Track>) {
        trackList = newListTrack
        notifyDataSetChanged()
    }
}
