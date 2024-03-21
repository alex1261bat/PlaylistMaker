package com.example.playlistmaker.ui.media

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemPlaylistDataBinding

class PlaylistDataMenuItemAdapter :
    RecyclerView.Adapter<PlaylistDataMenuItemAdapter.PlaylistDataMenuItemViewHolder>() {

    private var items = emptyList<PlaylistDataMenuItem>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistDataMenuItemViewHolder {
        val binding = ItemPlaylistDataBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistDataMenuItemViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PlaylistDataMenuItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    fun updateItems(newItems: List<PlaylistDataMenuItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    class PlaylistDataMenuItemViewHolder(
        private val binding: ItemPlaylistDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlaylistDataMenuItem) {
            with(binding) {
                tvText.text = root.context.getString(item.textResId)
                root.setOnClickListener { item.onClick() }
            }
        }
    }
}
