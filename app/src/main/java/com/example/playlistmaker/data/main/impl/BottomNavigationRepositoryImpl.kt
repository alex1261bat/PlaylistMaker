package com.example.playlistmaker.data.main.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.data.main.BottomNavigationRepository

class BottomNavigationRepositoryImpl : BottomNavigationRepository {
    private val bottomNavigationVisibility = MutableLiveData(true)
    override val isBottomNavigationVisible: LiveData<Boolean> = bottomNavigationVisibility

    override fun setBottomNavigationVisibility(isVisible: Boolean) {
        bottomNavigationVisibility.value = isVisible
    }
}
