package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.search.impl.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.search.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.network.impl.RetrofitNetworkClient
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.data.search.SearchHistoryRepository
import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.data.search.TrackRepository
import com.example.playlistmaker.data.settings.ApplicationThemeRepository
import com.example.playlistmaker.data.settings.impl.ApplicationThemeRepositoryImpl
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.search.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.settings.ApplicationThemeInteractor
import com.example.playlistmaker.domain.settings.impl.ApplicationThemeInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import com.example.playlistmaker.ui.main.PLAYLIST_MAKER_PREFERENCES

object Creator {
    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context))
    }

    private fun getSearchHistoryRepository(
        sharedPreferences: SharedPreferences
    ): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(sharedPreferences)
    }

    fun provideSearchHistoryInteractor(
        sharedPreferences: SharedPreferences
    ): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(sharedPreferences))
    }

    private fun getApplicationThemeRepository(
        sharedPreferences: SharedPreferences,
        context: Context
    ): ApplicationThemeRepository {
        return ApplicationThemeRepositoryImpl(sharedPreferences, context)
    }

    fun provideApplicationThemeInteractor(
        sharedPreferences: SharedPreferences,
        context: Context
    ): ApplicationThemeInteractor {
        return ApplicationThemeInteractorImpl(
            getApplicationThemeRepository(sharedPreferences, context))
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(context))
    }

    fun providePlayerInteractor(
        sharedPreferences: SharedPreferences): PlayerInteractor {
        return PlayerInteractorImpl(getSearchHistoryRepository(sharedPreferences))
    }

    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Application.MODE_PRIVATE)
    }
}
