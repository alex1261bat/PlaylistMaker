package com.example.playlistmaker.domain.main

import androidx.lifecycle.LiveData

interface BottomNavigationInteractor {
    val isBottomNavigationVisible: LiveData<Boolean>

    fun setBottomNavigationVisibility(isVisible: Boolean)
}
