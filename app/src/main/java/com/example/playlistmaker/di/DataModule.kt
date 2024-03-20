package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.data.db.PlaylistMakerDb
import com.example.playlistmaker.util.ListConverter
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single<PlaylistMakerDb> {
        Room.databaseBuilder(androidContext(), PlaylistMakerDb::class.java, PlaylistMakerDb.DB_NAME)
            .addTypeConverter(ListConverter(get<Gson>()))
            .fallbackToDestructiveMigration()
            .build()
    }
}
