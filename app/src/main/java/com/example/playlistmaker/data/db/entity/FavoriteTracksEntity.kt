package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class FavoriteTracksEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo("track_name")
    val trackName: String,
    @ColumnInfo("artist_name")
    val artistName: String,
    @ColumnInfo("track_time_millis")
    val trackTime: String,
    @ColumnInfo("artwork_url")
    val artworkUrl100: String,
    @ColumnInfo("collection_name")
    val collectionName: String?,
    @ColumnInfo("release_date")
    val releaseDate: String?,
    @ColumnInfo("primary_genre_url")
    val primaryGenreName: String,
    @ColumnInfo("country")
    val country: String,
    @ColumnInfo("preview_url")
    val previewUrl: String?,
    @ColumnInfo("created_at")
    val createdAt: Long
)
