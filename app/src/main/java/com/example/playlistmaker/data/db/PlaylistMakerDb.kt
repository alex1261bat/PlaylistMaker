package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [TrackEntity::class])
abstract class PlaylistMakerDb : RoomDatabase() {

    companion object {
        const val DB_NAME = "playlistMaker.db"
    }

    abstract fun trackDao(): TrackDao
}
