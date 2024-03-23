package com.example.playlistmaker.data.sharing

import com.example.playlistmaker.domain.model.Track

interface ExternalNavigator {
    fun openShareLink()
    fun openEmail()
    fun openUserAgreement()
    fun sharePlaylist(name: String, description: String, tracks: List<Track>)
}
