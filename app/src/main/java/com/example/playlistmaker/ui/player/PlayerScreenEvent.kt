package com.example.playlistmaker.ui.player

sealed class PlayerScreenEvent {
    object OpenPlaylistsBottomSheet : PlayerScreenEvent()
    object ClosePlaylistsBottomSheet : PlayerScreenEvent()
    data class ShowAddedTrackMessage(val playlistTitle: String) : PlayerScreenEvent()
    data class ShowTrackAlreadyInPlaylistMessage(val playlistTitle: String) : PlayerScreenEvent()
    object NavigateToCreatePlaylistFragment : PlayerScreenEvent()
}
