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
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
    private var binding: ActivitySearchBinding? = null
    private var trackAdapter: TrackAdapter? = null
    private var historyAdapter: TrackAdapter? = null
    private val viewModel: SearchViewModel by viewModel<SearchViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initObservers()
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            binding?.tbSearchBack?.setOnClickListener { finish() }
            ivUpdate.setOnClickListener { viewModel.clickUpdateButton() }
            ivClearIcon.setOnClickListener { viewModel.clickClearIcon() }
            initHistoryListRecyclerView()
            initTrackListRecyclerView()
        }

        initSearchEditText()
        initClearHistoryButton()
    }

    private fun initTrackListRecyclerView() {
        trackAdapter = TrackAdapter(viewModel::clickTrack)
        binding?.rvTrackList?.adapter = trackAdapter
    }

    private fun initHistoryListRecyclerView() {
        historyAdapter = TrackAdapter(viewModel::clickTrack)
        binding?.rvHistoryList?.adapter = historyAdapter
    }

    private fun initSearchEditText(): EditText? {
        return binding?.etSearch?.apply {
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
        binding?.ivClearHistory?.setOnClickListener {
            viewModel.clickClearHistoryButton() }
    }

    private fun initObservers() {
        viewModel.state.observe(this) {
            binding?.apply {
                trackAdapter?.updateData(it.trackList)
                rvTrackList.isVisible = it.trackList.isNotEmpty()
                historyAdapter?.updateData(it.searchHistoryList)
                rvHistoryList.isVisible = (it.searchHistoryList.isNotEmpty()
                        && it.historyVisibility)
                ivClearIcon.isVisible = it.clearIconVisibility
                ivClearHistory.isVisible = it.clearHistoryButtonVisibility
                tvYouSearched.isVisible = it.youSearchedVisibility
                llConnectionError.isVisible = it.connectionErrorVisibility
                tvNothingFound.isVisible = it.nothingFoundVisibility
                pbLoading.isVisible = it.progressBarVisibility
            }
        }

        viewModel.searchScreenEvent.observe(this) {
            when (it) {
                is SearchScreenEvent.OpenPlayerScreen -> {
                    startActivity(Intent(this, PlayerActivity()::class.java)) }

                is SearchScreenEvent.ClearSearchEditText -> binding?.etSearch?.text?.clear()

                else -> hideKeyboard()
            }
        }
    }

    private fun hideKeyboard() {
        val hide = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        hide.hideSoftInputFromWindow(binding?.ivClearIcon?.windowToken, 0)
    }
}
