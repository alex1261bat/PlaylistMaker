package com.example.playlistmaker.domain.settings

import com.example.playlistmaker.domain.settings.model.ApplicationTheme

interface ApplicationThemeInteractor {
    fun getApplicationTheme(): ApplicationTheme
    fun setApplicationTheme(applicationTheme: ApplicationTheme)
}
