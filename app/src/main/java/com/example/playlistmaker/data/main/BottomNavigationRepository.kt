package com.example.playlistmaker.data.main

import androidx.lifecycle.LiveData

interface BottomNavigationRepository {
    val isBottomNavigationVisible: LiveData<Boolean>

    fun setBottomNavigationVisibility(isVisible: Boolean)
}
