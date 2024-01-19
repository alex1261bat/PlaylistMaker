package com.example.playlistmaker.app

import android.app.Application
import com.example.playlistmaker.creator.Creator

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.provideApplicationThemeInteractor(
            Creator.provideSharedPreferences(this),
            this
        )
    }
}
