package com.example.playlistmaker.domain.sharing.impl

import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator) : SharingInteractor {

    override fun openShareLink() {
        externalNavigator.openShareLink()
    }

    override fun openEmail() {
        externalNavigator.openEmail()
    }

    override fun openUserAgreement() {
        externalNavigator.openUserAgreement()
    }
}
