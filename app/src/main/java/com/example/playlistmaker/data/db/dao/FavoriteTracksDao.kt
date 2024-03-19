package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.FavoriteTracksEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToFavorite(favoriteTracksEntity: FavoriteTracksEntity)

    @Delete
    suspend fun deleteTrackFromFavorite(favoriteTracksEntity: FavoriteTracksEntity)

    @Query("SELECT * FROM favorite_tracks ORDER BY created_at DESC")
    fun getFavoriteTracks(): Flow<List<FavoriteTracksEntity>>

    @Query("SELECT id FROM favorite_tracks")
    fun getFavoriteTracksIds(): List<String>
}
