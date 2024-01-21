package com.example.playlistmaker.ui.search

sealed class SearchScreenEvent {
    object OpenPlayerScreen : SearchScreenEvent()
    object ClearSearchEditText : SearchScreenEvent()
    object HideKeyboard : SearchScreenEvent()
}
