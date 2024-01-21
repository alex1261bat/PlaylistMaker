package com.example.playlistmaker.data.settings.impl

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.playlistmaker.data.settings.ApplicationThemeRepository
import com.example.playlistmaker.domain.settings.model.ApplicationTheme

class ApplicationThemeRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val context: Context
) : ApplicationThemeRepository {
    private var darkTheme = ApplicationTheme.LIGHT

    companion object {
        const val DARK_THEME_KEY = "dark_theme"
    }

    init {
        setApplicationTheme(getApplicationTheme())
    }

    override fun getApplicationTheme(): ApplicationTheme {
        if (sharedPreferences.contains(DARK_THEME_KEY)) {
            darkTheme =
                ApplicationTheme.valueOf(sharedPreferences.getString(DARK_THEME_KEY, "").toString())
        } else {
            when(context.resources.configuration.uiMode.and(UI_MODE_NIGHT_MASK)) {
                UI_MODE_NIGHT_YES -> darkTheme = ApplicationTheme.DARK
            }
        }

        return darkTheme
    }

    override fun setApplicationTheme(applicationTheme: ApplicationTheme) {
        val nightMode = when (applicationTheme) {
            ApplicationTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            ApplicationTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
        saveApplicationTheme(applicationTheme)
    }

    private fun saveApplicationTheme(applicationTheme: ApplicationTheme) {
        sharedPreferences.edit {
            putString(DARK_THEME_KEY, applicationTheme.name)
        }
    }
}
