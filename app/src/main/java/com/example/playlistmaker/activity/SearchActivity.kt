package com.example.playlistmaker.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.adapter.ITunesSearchAPI
import com.example.playlistmaker.adapter.TrackAdapter
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.model.SearchHistory
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.model.TrackResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity(), TrackAdapter.Listener {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchEditText: EditText
    private lateinit var nothingFoundPlaceholder: TextView
    private lateinit var connectionErrorPlaceholder: LinearLayout
    private lateinit var trackAdapter: TrackAdapter
    private var savedText = ""
    private val trackList = ArrayList<Track>()
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private lateinit var searchHistory: SearchHistory

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesSearchAPI::class.java)

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        trackAdapter = TrackAdapter(trackList, this)
        searchHistory = SearchHistory(getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE))
        val historyAdapter = TrackAdapter(searchHistory.getHistoryList(), this)
        val trackRecyclerView = binding.trackRecyclerView
        trackRecyclerView.adapter = trackAdapter
        val backButton = binding.searchBackButton
        val clearButton = binding.clearIcon
        searchEditText = binding.searchEditText
        nothingFoundPlaceholder = binding.nothingFound
        connectionErrorPlaceholder = binding.connectionError
        val updateButton = binding.updateButton
        val clearHistoryButton = binding.clearHistoryButton
        val youSearched = binding.youSearched

        clearHistoryButton.setOnClickListener { 
            searchHistory.clearHistory()
            trackList.clear()
            youSearched.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
            historyAdapter.notifyDataSetChanged()
            trackAdapter.notifyDataSetChanged()
            trackRecyclerView.adapter = trackAdapter
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (trackList.isNotEmpty() && searchHistory.getHistoryList().isNotEmpty()) {
                if (hasFocus && searchEditText.text.isEmpty()) {
                    trackRecyclerView.adapter = historyAdapter
                    youSearched.visibility = View.VISIBLE
                    clearHistoryButton.visibility = View.VISIBLE
                } else {
                    youSearched.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                }
            }
        }
        
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchEditText.text.isNotEmpty()) {
                    findTrack(searchEditText.text.toString())
                }
            }
            false
        }

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyBoard()
            trackList.clear()
            nothingFoundPlaceholder.visibility = View.GONE
            connectionErrorPlaceholder.visibility = View.GONE
            trackAdapter.notifyDataSetChanged()
        }

        updateButton.setOnClickListener {
            findTrack(searchEditText.text.toString())
            connectionErrorPlaceholder.visibility = View.GONE
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
                clearButton.visibility = clearButtonVisibility(s)
                savedText = searchEditText.text.toString()

                if (trackList.isNotEmpty() && searchHistory.getHistoryList().isNotEmpty()) {
                    if (searchEditText.hasFocus() && s?.isEmpty() == true) {
                        trackRecyclerView.adapter = historyAdapter
                        youSearched.visibility = View.VISIBLE
                        clearHistoryButton.visibility = View.VISIBLE
                    } else {
                        youSearched.visibility = View.GONE
                        clearHistoryButton.visibility = View.GONE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        searchEditText.addTextChangedListener(searchTextWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, savedText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedText = savedInstanceState.getString(SEARCH_TEXT, "")
        searchEditText.setText(savedText)
    }

    private fun hideKeyBoard() {
        val view = this.currentFocus

        if (view != null) {
            val hide = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hide.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun findTrack(searchText: String) {
        iTunesService.search(searchText)
            .enqueue(object : Callback<TrackResponse> {

                override fun onResponse(call: Call<TrackResponse>,
                                        response: Response<TrackResponse>) {
                    if (response.code() == 200) {
                        trackList.clear()
                        nothingFoundPlaceholder.visibility = View.GONE

                        if (response.body()?.results?.isNotEmpty() == true) {
                            trackList.addAll(response.body()?.results!!)
                            trackAdapter.notifyDataSetChanged()
                        }

                        if (trackList.isEmpty()) {
                            nothingFoundPlaceholder.visibility = View.VISIBLE
                        }

                    } else {
                        connectionErrorPlaceholder.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    connectionErrorPlaceholder.visibility = View.VISIBLE
                }
            })
    }

    override fun onClick(track: Track) {
        searchHistory.addTrackToHistory(track)
    }
}
