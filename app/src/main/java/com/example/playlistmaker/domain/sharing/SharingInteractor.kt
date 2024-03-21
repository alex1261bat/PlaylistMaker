package com.example.playlistmaker.domain.sharing

import com.example.playlistmaker.domain.model.Track

interface SharingInteractor {
    fun openShareLink()
    fun openEmail()
    fun openUserAgreement()
    fun sharePlaylist(name: String, description: String, tracks: List<Track>)
}
