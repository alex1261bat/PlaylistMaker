package com.example.playlistmaker.ui.main.view_model

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.main.BottomNavigationInteractor

class MainViewModel(bottomNavigationInteractor: BottomNavigationInteractor) : ViewModel() {
    val isBottomNavigationVisible = bottomNavigationInteractor.isBottomNavigationVisible
}
