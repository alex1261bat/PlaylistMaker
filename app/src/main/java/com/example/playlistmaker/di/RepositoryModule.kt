package com.example.playlistmaker.di

import com.example.playlistmaker.data.db.repository.FavoriteTracksRepository
import com.example.playlistmaker.data.db.converters.PlaylistMakerDbConverter
import com.example.playlistmaker.data.db.impl.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.data.db.impl.PlaylistCoverRepositoryImpl
import com.example.playlistmaker.data.db.impl.PlaylistRepositoryImpl
import com.example.playlistmaker.data.db.repository.PlaylistCoverRepository
import com.example.playlistmaker.data.db.repository.PlaylistRepository
import com.example.playlistmaker.data.main.BottomNavigationRepository
import com.example.playlistmaker.data.main.impl.BottomNavigationRepositoryImpl
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

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }

    single<PlaylistCoverRepository> {
        PlaylistCoverRepositoryImpl(get())
    }

    single<BottomNavigationRepository> {
        BottomNavigationRepositoryImpl()
    }

    factory { PlaylistMakerDbConverter() }
}
