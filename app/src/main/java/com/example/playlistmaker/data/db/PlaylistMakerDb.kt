package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.playlistmaker.data.db.dao.FavoriteTracksDao
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.PlaylistTracksDao
import com.example.playlistmaker.data.db.entity.FavoriteTracksEntity
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.PlaylistTracksEntity
import com.example.playlistmaker.util.ListConverter

@Database(version = 2,
          entities = [FavoriteTracksEntity::class,
                      PlaylistEntity::class,
                      PlaylistTracksEntity::class])
@TypeConverters(ListConverter::class)
abstract class PlaylistMakerDb : RoomDatabase() {

    companion object {
        const val DB_NAME = "playlistMaker.db"
    }

    abstract fun favoriteTracksDao(): FavoriteTracksDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTracksDao(): PlaylistTracksDao
}
