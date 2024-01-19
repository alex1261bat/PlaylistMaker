package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.ui.search.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {
    private var binding: ActivitySearchBinding? = null
    private var trackAdapter: TrackAdapter? = null
    private var historyAdapter: TrackAdapter? = null
    private lateinit var viewModel: SearchViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        viewModel = ViewModelProvider(this).get<SearchViewModel>()

        initObservers()
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            binding?.searchBackButton?.setOnClickListener { finish() }
            updateButton.setOnClickListener { viewModel.clickUpdateButton() }
            clearIcon.setOnClickListener { viewModel.clickClearIcon() }
            initHistoryListRecyclerView()
            initTrackListRecyclerView()
        }

        initSearchEditText()
        initClearHistoryButton()
    }

    private fun initTrackListRecyclerView() {
        trackAdapter = TrackAdapter(viewModel::clickTrack)
        binding?.trackRecyclerView?.adapter = trackAdapter
    }

    private fun initHistoryListRecyclerView() {
        historyAdapter = TrackAdapter(viewModel::clickTrack)
        binding?.trackHistoryRecyclerView?.adapter = historyAdapter
    }

    private fun initSearchEditText(): EditText? {
        return binding?.searchEditText?.apply {
            doOnTextChanged { text, _, _, _ ->
                viewModel.searchRequestIsChanged(text?.toString()?.trim() ?: "")
            }

            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.clickEnter()
                }

                return@setOnEditorActionListener false
            }

            setOnFocusChangeListener { _, hasFocus -> viewModel.searchFocusIsChanged(hasFocus) }
        }
    }

    private fun initClearHistoryButton() {
        binding?.clearHistoryButton?.setOnClickListener { viewModel.clickClearHistoryButton() }
    }

    private fun initObservers() {
        viewModel.state.observe(this) {
            binding?.apply {
                trackAdapter?.updateData(it.trackList)
                trackRecyclerView.isVisible = it.trackList.isNotEmpty()
                historyAdapter?.updateData(it.searchHistoryList)
                trackHistoryRecyclerView.isVisible = (it.searchHistoryList.isNotEmpty()
                        && it.historyVisibility)
                clearIcon.isVisible = it.clearIconVisibility
                clearHistoryButton.isVisible = it.clearHistoryButtonVisibility
                youSearched.isVisible = it.youSearchedVisibility
                connectionError.isVisible = it.connectionErrorVisibility
                nothingFound.isVisible = it.nothingFoundVisibility
                progressBar.isVisible = it.progressBarVisibility
            }
        }

        viewModel.searchScreenEvent.observe(this) {
            when (it) {
                is SearchScreenEvent.OpenPlayerScreen -> {
                    startActivity(Intent(this, PlayerActivity()::class.java)) }

                is SearchScreenEvent.ClearSearchEditText -> binding?.searchEditText?.text?.clear()

                else -> hideKeyboard()
            }
        }
    }

    private fun hideKeyboard() {
        val hide = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        hide.hideSoftInputFromWindow(binding?.clearIcon?.windowToken, 0)
    }
}
