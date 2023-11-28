package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.presentation.ui.main.DARK_THEME_KEY
import com.example.playlistmaker.presentation.ui.main.PLAYLIST_MAKER_PREFERENCES

class App: Application() {
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        if (sharedPreferences.contains(DARK_THEME_KEY)) {
            darkTheme = sharedPreferences.getString(DARK_THEME_KEY, "").toBoolean()
        } else {
            when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> darkTheme = true
            }
        }

        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}