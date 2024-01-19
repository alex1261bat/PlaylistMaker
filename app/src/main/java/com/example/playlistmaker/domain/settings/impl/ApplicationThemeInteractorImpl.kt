package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.data.settings.ApplicationThemeRepository
import com.example.playlistmaker.domain.settings.ApplicationThemeInteractor
import com.example.playlistmaker.domain.settings.model.ApplicationTheme

class ApplicationThemeInteractorImpl(
    private val applicationThemeRepository: ApplicationThemeRepository
) : ApplicationThemeInteractor {

    override fun getApplicationTheme(): ApplicationTheme =
        applicationThemeRepository.getApplicationTheme()

    override fun setApplicationTheme(applicationTheme: ApplicationTheme) {
        applicationThemeRepository.setApplicationTheme(applicationTheme)
    }
}
