package com.example.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.TrackInteractor
import com.example.playlistmaker.ui.search.SearchScreenEvent
import com.example.playlistmaker.ui.search.SearchScreenState
import com.example.playlistmaker.util.Resource
import com.example.playlistmaker.util.SingleLiveEvent
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {
    private var searchHistoryList = listOf<Track>()
    private var trackList = listOf<Track>()
    private var savedText = ""
    private val searchScreenState = MutableLiveData<SearchScreenState>()
    val state: LiveData<SearchScreenState> = searchScreenState
    val searchScreenEvent = SingleLiveEvent<SearchScreenEvent>()
    private val searchDebounce = debounce<Unit>(SEARCH_DEBOUNCE_DELAY_MILLIS, viewModelScope,
        false) { _ -> findTrack() }
    private val clickTrackDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY_MILLIS, viewModelScope,
        false) {track ->
        searchHistoryInteractor.addTrackToHistory(track)
        searchHistoryInteractor.saveTrackForPlaying(track)
        searchHistoryList = searchHistoryInteractor.getHistoryList()
        searchScreenEvent.postValue(SearchScreenEvent.OpenPlayerScreen)
    }

    companion object {
        const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
    }

    init {
        getHistoryList(searchHistoryInteractor)
    }

    fun searchRequestIsChanged(searchRequest: String) {
        savedText = searchRequest

        if (searchRequest.isNotBlank()) {
            searchScreenState.value = getCurrentScreenState()
                .copy(clearIconVisibility = true,
                    historyVisibility = false,
                    youSearchedVisibility = false,
                    clearHistoryButtonVisibility = false,
                    nothingFoundVisibility = false
            )
            searchDebounce(Unit)
        } else {
            searchScreenState.value = SearchScreenState(
                searchHistoryList = searchHistoryList,
                youSearchedVisibility = searchHistoryList.isNotEmpty(),
                clearHistoryButtonVisibility = searchHistoryList.isNotEmpty()
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
        searchDebounce(Unit)
        searchScreenState.value = getCurrentScreenState().copy(
            connectionErrorVisibility = false,
            historyVisibility = false
        )
    }

    fun clickEnter() {
        if (savedText.isNotBlank()) {
            searchDebounce(Unit)
        }
    }

    fun clickTrack(track: Track) {
        clickTrackDebounce(track)
    }

    private fun findTrack() {
        searchScreenState.value = getCurrentScreenState().copy(progressBarVisibility = true)
        viewModelScope.launch {
            trackInteractor.findTracks(savedText)
                .collect { foundTracks ->
                    if (foundTracks.data.isNullOrEmpty() &&
                        foundTracks.message == "Проверьте подключение к интернету"
                    ) {
                        handleFailureResponse()
                    } else {
                        handleSuccessResponse(foundTracks)
                    }
                }
        }
    }

    private fun getHistoryList(searchHistoryInteractor: SearchHistoryInteractor) {
        searchHistoryList = searchHistoryInteractor.getHistoryList()
        searchScreenState.value = SearchScreenState(
            searchHistoryList = searchHistoryList,
            youSearchedVisibility = searchHistoryList.isNotEmpty(),
            clearHistoryButtonVisibility = searchHistoryList.isNotEmpty()
        )
    }

    private fun getCurrentScreenState() = state.value ?: SearchScreenState()

    private fun handleSuccessResponse(response: Resource<List<Track>>) {
        trackList = response.data ?: listOf()

        searchScreenState.postValue(
            SearchScreenState(
                clearIconVisibility = savedText.isNotEmpty(),
                trackList = trackList,
                searchHistoryList = searchHistoryList,
                nothingFoundVisibility = trackList.isEmpty(),
                historyVisibility = false
            )
        )
    }

    private fun handleFailureResponse() {
        searchScreenState.postValue(
            SearchScreenState(
                clearIconVisibility = savedText.isNotEmpty(),
                searchHistoryList = searchHistoryList,
                connectionErrorVisibility = true,
                historyVisibility = false
            )
        )
    }
}
