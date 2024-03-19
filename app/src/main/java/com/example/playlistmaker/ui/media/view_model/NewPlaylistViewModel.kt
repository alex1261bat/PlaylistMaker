package com.example.playlistmaker.ui.media.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.main.BottomNavigationInteractor
import com.example.playlistmaker.ui.media.NewPlaylistEvent
import com.example.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewPlaylistViewModel(
    private val bottomNavigationInteractor: BottomNavigationInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    companion object {
        const val KEY_PLAYLIST_COVER_URI = "key_playlist_cover_uri"
    }

    private var playlistTitle = ""
    private var playlistDescription: String? = null
    private val playlistCoverUri: MutableLiveData<Uri?> = MutableLiveData()
    val playlistCover: LiveData<Uri?> = playlistCoverUri
    private val isButtonCreateEnabled = MutableLiveData(false)
    val buttonCreateEnabled: LiveData<Boolean> = isButtonCreateEnabled
    val playlistEvent = SingleLiveEvent<NewPlaylistEvent>()

    fun onBackPressed() {
        if (checkPlaylistCreationIsNotFinished()) {
            playlistEvent.value = NewPlaylistEvent.ShowBackConfirmationDialog
        } else {
            navigateBack()
        }
    }

    fun playlistTitleChanged(name: String) {
        playlistTitle = name
        isButtonCreateEnabled.value = playlistTitle.isNotEmpty()
    }

    fun playlistDescriptionChanged(description: String) {
        playlistDescription = description
    }

    fun createPlaylistClick() {
        if (playlistTitle.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                playlistInteractor.addPlaylist(
                    playlistTitle,
                    playlistDescription,
                    playlistCover.value
                )
                withContext(Dispatchers.Main) {
                    playlistEvent.value = NewPlaylistEvent.SetPlaylistCreatedResult(playlistTitle)
                    navigateBack()
                }
            }
        }
    }

    fun backPressConfirmed() = navigateBack()

    fun playlistCoverSelected(url: Uri) {
        playlistCoverUri.value = url
    }

    private fun checkPlaylistCreationIsNotFinished(): Boolean {
        return playlistTitle.isNotEmpty() ||
                !playlistDescription.isNullOrEmpty() ||
                playlistCover.value != null
    }

    private fun navigateBack() {
        playlistEvent.value = NewPlaylistEvent.NavigateBack
        bottomNavigationInteractor.setBottomNavigationVisibility(true)
    }
}
