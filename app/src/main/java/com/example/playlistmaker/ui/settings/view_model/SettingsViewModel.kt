package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.playlistmaker.domain.settings.ApplicationThemeInteractor
import com.example.playlistmaker.domain.settings.model.ApplicationTheme
import com.example.playlistmaker.domain.sharing.SharingInteractor

class SettingsViewModel(
    private val applicationThemeInteractor: ApplicationThemeInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {
    private val applicationTheme = MutableLiveData(applicationThemeInteractor
        .getApplicationTheme())
    val appTheme: LiveData<Boolean> = applicationTheme.map { it == ApplicationTheme.DARK }

    fun clickThemeSwitcher(isChecked: Boolean) {
        val appTheme = if (isChecked) ApplicationTheme.DARK else ApplicationTheme.LIGHT
        applicationThemeInteractor.setApplicationTheme(appTheme)
    }

    fun clickShareButton() = sharingInteractor.openShareLink()

    fun clickSendToSupportButton() = sharingInteractor.openEmail()

    fun clickUserAgreementButton() = sharingInteractor.openUserAgreement()
}
