package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.data.db.entity.PlaylistTracksEntity

@Dao
interface PlaylistTracksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToPlaylist(playlistTracksEntity: PlaylistTracksEntity)
}
