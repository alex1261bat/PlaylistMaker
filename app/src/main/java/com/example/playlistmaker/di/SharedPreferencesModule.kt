package com.example.playlistmaker.di

import android.app.Application
import android.content.SharedPreferences
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"

val sharedPreferencesModule = module {

    single<SharedPreferences> {
        androidContext().getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Application.MODE_PRIVATE)
    }

    factory<Gson> { Gson() }
}
