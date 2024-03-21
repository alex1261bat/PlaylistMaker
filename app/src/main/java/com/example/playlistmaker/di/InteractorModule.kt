package com.example.playlistmaker.di

import com.example.playlistmaker.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.db.impl.FavoriteTracksInteractorImpl
import com.example.playlistmaker.domain.db.impl.PlaylistInteractorImpl
import com.example.playlistmaker.domain.main.BottomNavigationInteractor
import com.example.playlistmaker.domain.main.impl.BottomNavigationInteractorImpl
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.search.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.settings.ApplicationThemeInteractor
import com.example.playlistmaker.domain.settings.impl.ApplicationThemeInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<PlayerInteractor> {
        PlayerInteractorImpl(searchHistoryRepository = get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(searchHistoryRepository = get())
    }

    single<TrackInteractor> {
        TrackInteractorImpl(repository = get())
    }

    single<ApplicationThemeInteractor>(createdAtStart = true) {
        ApplicationThemeInteractorImpl(applicationThemeRepository = get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(externalNavigator = get())
    }

    single<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get(), get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get(), get(), get(), get())
    }

    single<BottomNavigationInteractor> {
        BottomNavigationInteractorImpl(get())
    }
}
