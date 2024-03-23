package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistTracksEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTracksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToPlaylist(playlistTracksEntity: PlaylistTracksEntity)

    @Query("DELETE FROM playlist_tracks WHERE id = (:trackId)")
    suspend fun deleteTrack(trackId: String)

    @Query("SELECT * FROM playlist_tracks WHERE id IN (:tracksIds)")
    fun getPlaylistTracks(tracksIds: List<String>): Flow<List<PlaylistTracksEntity>>
}
