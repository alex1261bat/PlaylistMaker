package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToFavorite(trackEntity: TrackEntity)

    @Delete
    suspend fun deleteTrackFromFavorite(trackEntity: TrackEntity)

    @Query("SELECT * FROM favorite_tracks ORDER BY created_at DESC")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT id FROM favorite_tracks")
    fun getFavoriteTracksIds(): List<String>
}
