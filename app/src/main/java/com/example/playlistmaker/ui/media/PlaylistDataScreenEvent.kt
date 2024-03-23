package com.example.playlistmaker.ui.media

import com.example.playlistmaker.domain.model.Playlist

sealed class PlaylistDataScreenEvent {
    object NavigateBack : PlaylistDataScreenEvent()
    data class SetMenuVisibility(val isVisible: Boolean) : PlaylistDataScreenEvent()
    object ShowMessageNoTracksInPlaylist : PlaylistDataScreenEvent()
    object OpenPlayerScreen : PlaylistDataScreenEvent()
    object ShowDeleteTrackDialog : PlaylistDataScreenEvent()
    data class ShowDeletePlaylistDialog(val playlistName: String) : PlaylistDataScreenEvent()
    data class NavigateToEditPlaylist(val playlist: Playlist) : PlaylistDataScreenEvent()
}
