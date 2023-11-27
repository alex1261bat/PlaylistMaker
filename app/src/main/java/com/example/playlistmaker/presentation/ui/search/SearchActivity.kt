package com.example.playlistmaker.presentation.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.Creator
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TrackInteractor.TrackConsumer
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.main.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.presentation.ui.media.MediaActivity
import com.google.gson.Gson

class SearchActivity : AppCompatActivity(), TrackAdapter.Listener {
    private var binding: ActivitySearchBinding? = null
    private var trackAdapter: TrackAdapter? = null
    private var historyAdapter: TrackAdapter? = null
    private var savedText = ""
    private val trackList = ArrayList<Track>()
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { findTrack(binding?.searchEditText?.text.toString()) }
    private val trackInteractor = Creator.provideTrackInteractor()
    private var searchHistoryInteractor: SearchHistoryInteractor? = null

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TRACK_DATA = "trackData"
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        trackAdapter = TrackAdapter(trackList, this)
        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(
            getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE))
        historyAdapter = TrackAdapter(searchHistoryInteractor!!.getHistoryList(), this)
        binding?.trackRecyclerView?.adapter = trackAdapter

        binding?.clearHistoryButton?.setOnClickListener {
            searchHistoryInteractor!!.clearHistory()
            trackList.clear()
            binding?.youSearched?.visibility = View.GONE
            binding?.clearHistoryButton?.visibility = View.GONE
            historyAdapter?.notifyDataSetChanged()
            trackAdapter?.notifyDataSetChanged()
            binding?.trackRecyclerView?.adapter = trackAdapter
        }


        binding?.searchEditText?.setOnFocusChangeListener { _, hasFocus ->
            if (searchHistoryInteractor!!.getHistoryList().isNotEmpty() && hasFocus
                && binding?.searchEditText!!.text.isBlank()) {
                binding?.trackRecyclerView?.adapter = historyAdapter
                binding?.youSearched?.visibility = View.VISIBLE
                binding?.clearHistoryButton?.visibility = View.VISIBLE
                hideKeyBoard()
            } else {
                binding?.youSearched?.visibility = View.GONE
                binding?.clearHistoryButton?.visibility = View.GONE
            }
        }
        
        binding?.searchEditText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                findTrack(binding?.searchEditText!!.text.toString())
            }
            false
        }

            binding?.searchBackButton?.setOnClickListener {
            finish()
        }

        binding?.clearIcon?.setOnClickListener {
            binding?.searchEditText?.setText("")
            hideKeyBoard()
            binding?.nothingFound?.visibility = View.GONE
            binding?.connectionError?.visibility = View.GONE
            trackList.clear()
            trackAdapter?.notifyDataSetChanged()
        }

        binding?.updateButton?.setOnClickListener {
            findTrack(binding?.searchEditText?.text.toString())
            binding?.connectionError?.visibility = View.GONE
        }

        fun clearButtonVisibility(s: CharSequence?): Int {
            return if (s.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding?.clearIcon?.visibility = clearButtonVisibility(s)
                savedText = binding?.searchEditText?.text.toString()
                binding?.trackRecyclerView?.adapter = trackAdapter

                searchDebounce()

                if (binding?.searchEditText?.text.toString().isBlank()) {
                    binding?.connectionError?.visibility = View.GONE
                    binding?.nothingFound?.visibility = View.GONE
                }

                if (searchHistoryInteractor!!.getHistoryList().isNotEmpty()
                    && binding?.searchEditText!!.hasFocus() && s?.isEmpty() == true) {
                    binding?.trackRecyclerView?.adapter = historyAdapter
                    binding?.youSearched?.visibility = View.VISIBLE
                    binding?.clearHistoryButton?.visibility = View.VISIBLE
                    hideKeyBoard()
                } else {
                    binding?.youSearched?.visibility = View.GONE
                    binding?.clearHistoryButton?.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        binding?.searchEditText!!.addTextChangedListener(searchTextWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, savedText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedText = savedInstanceState.getString(SEARCH_TEXT, "")
        binding?.searchEditText?.setText(savedText)
    }

    private fun hideKeyBoard() {
        val view = this.currentFocus

        if (view != null) {
            val hide = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hide.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun findTrack(searchText: String) {
        trackList.clear()
        trackAdapter?.notifyDataSetChanged()

        if(searchText.isNotBlank()) {
            binding?.nothingFound?.visibility = View.GONE
            binding?.connectionError?.visibility = View.GONE
            binding?.progressBar?.visibility = View.VISIBLE

            trackInteractor.findTracks(searchText, consumer = object : TrackConsumer {
                override fun consume(foundTracks: List<Track>?) {
                    handler.post{
                        binding?.progressBar?.visibility = View.GONE

                        if (!foundTracks.isNullOrEmpty()) {
                            trackList.addAll(foundTracks)
                            trackAdapter?.notifyDataSetChanged()
                        } else if (foundTracks?.isEmpty() == true) {
                            binding?.nothingFound?.visibility = View.VISIBLE
                        } else {
                            binding?.connectionError?.visibility = View.VISIBLE
                        }
                    }
                }
            })

            hideKeyBoard()
        }
    }

    override fun onClick(track: Track) {
        if (clickDebounce()) {
            searchHistoryInteractor?.addTrackToHistory(track)
            historyAdapter?.notifyDataSetChanged()

            val gson = Gson()
            val trackIntent = Intent(this, MediaActivity::class.java)

            trackIntent.putExtra(TRACK_DATA, gson.toJson(track))
            startActivity(trackIntent)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed

        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
}
