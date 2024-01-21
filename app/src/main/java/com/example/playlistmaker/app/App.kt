package com.example.playlistmaker.app

import android.app.Application
import com.example.playlistmaker.creator.Creator

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = Creator.provideSharedPreferences(this)
        Creator.provideApplicationThemeInteractor(sharedPreferences, this)
    }
}
