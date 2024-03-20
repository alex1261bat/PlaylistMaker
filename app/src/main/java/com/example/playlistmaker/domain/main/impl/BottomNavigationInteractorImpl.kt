package com.example.playlistmaker.domain.main.impl

import androidx.lifecycle.LiveData
import com.example.playlistmaker.data.main.BottomNavigationRepository
import com.example.playlistmaker.domain.main.BottomNavigationInteractor

class BottomNavigationInteractorImpl(
    private val bottomNavigationRepository: BottomNavigationRepository
) : BottomNavigationInteractor {
    override val isBottomNavigationVisible: LiveData<Boolean> =
        bottomNavigationRepository.isBottomNavigationVisible

    override fun setBottomNavigationVisibility(isVisible: Boolean) {
        bottomNavigationRepository.setBottomNavigationVisibility(isVisible)
    }
}
