package com.example.playlistmaker.ui.media

sealed class PlaylistScreenEvent{
    object NavigateToNewPlaylist : PlaylistScreenEvent()
    data class NavigateToPlaylistData(val playlistId: String) : PlaylistScreenEvent()
}
