package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo("title")
    val title: String,
    @ColumnInfo("description")
    val description: String?,
    @ColumnInfo("playlist_cover_uri")
    val playlistCoverUri: String?,
    @ColumnInfo("tracks_ids")
    val tracksIds: List<String> = listOf(),
    @ColumnInfo("tracks_count")
    val tracksCount: Int = 0
)
