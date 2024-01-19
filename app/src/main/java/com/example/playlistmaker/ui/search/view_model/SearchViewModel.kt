package com.example.playlistmaker.ui.search.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.ui.search.SearchScreenEvent
import com.example.playlistmaker.ui.search.SearchScreenState
import com.example.playlistmaker.util.Resource
import com.example.playlistmaker.util.SingleLiveEvent

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val trackInteractor = Creator.provideTrackInteractor(application)
    private val sharedPreferences = Creator.provideSharedPreferences(application)
    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(sharedPreferences)
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { findTrack() }
    private var searchHistoryList = listOf<Track>()
    private var trackList = listOf<Track>()
    private var isClickAllowed = true
    private var savedText = ""
    private val searchScreenState = MutableLiveData<SearchScreenState>()
    val state: LiveData<SearchScreenState> = searchScreenState
    val searchScreenEvent = SingleLiveEvent<SearchScreenEvent>()

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    init {
        getHistoryList()
    }

    override fun onCleared() {
        handler.removeCallbacks(searchRunnable)
        super.onCleared()
    }

    fun searchRequestIsChanged(searchRequest: String) {
        savedText = searchRequest
        handler.removeCallbacks(searchRunnable)

        if (searchRequest.isNotBlank()) {
            searchScreenState.value = getCurrentScreenState().copy(
                clearIconVisibility = true,
                historyVisibility = false,
                youSearchedVisibility = false,
                clearHistoryButtonVisibility = false
            )
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        } else {
            searchScreenState.value = SearchScreenState(
                searchHistoryList = searchHistoryList,
                youSearchedVisibility = searchHistoryList.isNotEmpty(),
                clearHistoryButtonVisibility = searchHistoryList.isNotEmpty(),
                historyVisibility = true
            )
        }
    }

    fun searchFocusIsChanged(hasFocus: Boolean) {
        if (hasFocus) {
            searchScreenState.value = getCurrentScreenState()
                .copy(youSearchedVisibility = false,
                    clearHistoryButtonVisibility = false,
                    historyVisibility = false
                )
        } else {
            searchScreenState.value = getCurrentScreenState()
                .copy(searchHistoryList = searchHistoryList,
                    youSearchedVisibility = searchHistoryList.isNotEmpty(),
                    clearHistoryButtonVisibility = searchHistoryList.isNotEmpty()
                )
        }
    }

    fun clickClearHistoryButton() {
        searchHistoryInteractor.clearHistory()
        searchHistoryList = searchHistoryInteractor.getHistoryList()
        searchScreenState.value = getCurrentScreenState()
            .copy(clearHistoryButtonVisibility = false,
                youSearchedVisibility = false
            )
    }

    fun clickClearIcon() {
        searchScreenState.value = SearchScreenState(
            searchHistoryList = searchHistoryList,
            youSearchedVisibility = searchHistoryList.isNotEmpty(),
            clearHistoryButtonVisibility = searchHistoryList.isNotEmpty()
        )
        searchScreenEvent.value = SearchScreenEvent.HideKeyboard
        searchScreenEvent.value = SearchScreenEvent.ClearSearchEditText
    }

    fun clickUpdateButton() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun clickEnter() {
        if (savedText.isNotBlank()) {
            handler.removeCallbacks(searchRunnable)
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }

    fun clickTrack(track: Track) {
        clickDebounce()
        searchHistoryInteractor.addTrackToHistory(track)
        searchHistoryInteractor.saveTrackForPlaying(track)
        searchHistoryList = searchHistoryInteractor.getHistoryList()
        searchScreenEvent.value = SearchScreenEvent.OpenPlayerScreen
    }

    private fun findTrack() {
        searchScreenState.value = getCurrentScreenState().copy(progressBarVisibility = true)
        val trackConsumer = object : TrackInteractor.TrackConsumer {
            override fun consume(foundTracks: Resource<List<Track>>) {
                if (foundTracks.data.isNullOrEmpty() &&
                    foundTracks.message == "Проверьте подключение к интернету"
                ) {
                    handleFailureResponse()
                } else {
                    handleSuccessResponse(foundTracks)
                }
            }
        }

        trackInteractor.findTracks(savedText, trackConsumer)
    }

    private fun getHistoryList() {
        searchHistoryList = searchHistoryInteractor.getHistoryList()
        searchScreenState.value = SearchScreenState(
            searchHistoryList = searchHistoryList,
            youSearchedVisibility = searchHistoryList.isNotEmpty(),
            clearHistoryButtonVisibility = searchHistoryList.isNotEmpty()
        )
    }

    private fun getCurrentScreenState() = state.value ?: SearchScreenState()

    private fun clickDebounce(): Boolean {
        val currentClickIsAllowed = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return currentClickIsAllowed
    }

    private fun handleSuccessResponse(response: Resource<List<Track>>) {
        trackList = response.data ?: listOf()

        searchScreenState.postValue(
            SearchScreenState(
                clearIconVisibility = savedText.isNotEmpty(),
                trackList = trackList,
                searchHistoryList = searchHistoryList,
                nothingFoundVisibility = trackList.isEmpty(),
                historyVisibility = trackList.isNotEmpty()
            )
        )
    }

    private fun handleFailureResponse() {
        searchScreenState.postValue(
            SearchScreenState(
                clearIconVisibility = savedText.isNotEmpty(),
                searchHistoryList = searchHistoryList,
                connectionErrorVisibility = true
            )
        )
    }
}
