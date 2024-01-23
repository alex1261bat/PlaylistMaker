package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.ui.media.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.ui.media.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel<PlayerViewModel> {
        PlayerViewModel(
            androidApplication(),
            playerInteractor = get(),
            mediaPlayer = MediaPlayer()
        )
    }

    viewModel<SearchViewModel> {
        SearchViewModel(
            trackInteractor = get(),
            searchHistoryInteractor = get()
        )
    }

    viewModel<SettingsViewModel> {
        SettingsViewModel(
            applicationThemeInteractor = get(),
            sharingInteractor = get()
        )
    }

    viewModel<FavoriteTracksViewModel> {
        FavoriteTracksViewModel()
    }

    viewModel<PlaylistViewModel> {
        PlaylistViewModel()
    }
}
