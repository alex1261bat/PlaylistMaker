package com.example.playlistmaker.ui.media

sealed class NewPlaylistEvent{
    object NavigateBack : NewPlaylistEvent()
    object ShowBackConfirmationDialog : NewPlaylistEvent()
    data class SetPlaylistCreatedResult(val playlistTitle: String) : NewPlaylistEvent()
}
