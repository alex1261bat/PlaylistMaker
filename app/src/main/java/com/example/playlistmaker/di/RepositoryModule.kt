package com.example.playlistmaker.di

import com.example.playlistmaker.data.db.FavoriteTracksRepository
import com.example.playlistmaker.data.db.converters.PlaylistMakerDbConverter
import com.example.playlistmaker.data.db.impl.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.data.search.SearchHistoryRepository
import com.example.playlistmaker.data.search.TrackRepository
import com.example.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.search.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.settings.ApplicationThemeRepository
import com.example.playlistmaker.data.settings.impl.ApplicationThemeRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<ApplicationThemeRepository> {
        ApplicationThemeRepositoryImpl(
            sharedPreferences = get(),
            context = androidContext()
        )
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            sharedPreferences = get(),
            gson = get()
        )
    }

    single<TrackRepository> {
        TrackRepositoryImpl(get(), get())
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    factory { PlaylistMakerDbConverter() }
}
