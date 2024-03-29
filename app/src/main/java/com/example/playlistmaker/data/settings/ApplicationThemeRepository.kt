package com.example.playlistmaker.data.settings

import com.example.playlistmaker.domain.settings.model.ApplicationTheme

interface ApplicationThemeRepository {
    fun getApplicationTheme(): ApplicationTheme

    fun setApplicationTheme(applicationTheme: ApplicationTheme)
}
