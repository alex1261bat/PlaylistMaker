package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var binding: FragmentSearchBinding? = null
    private var trackAdapter: TrackAdapter? = null
    private var historyAdapter: TrackAdapter? = null
    private val viewModel: SearchViewModel by viewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun initViews() {
        binding?.apply {
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
        viewModel.state.observe(viewLifecycleOwner) {
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

        viewModel.searchScreenEvent.observe(viewLifecycleOwner) {
            when (it) {
                is SearchScreenEvent.OpenPlayerScreen -> {
                    startActivity(Intent(requireContext(), PlayerActivity::class.java)) }

                is SearchScreenEvent.ClearSearchEditText -> binding?.etSearch?.text?.clear()

                else -> hideKeyboard()
            }
        }
    }

    private fun hideKeyboard() {
        val hide = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        hide.hideSoftInputFromWindow(binding?.ivClearIcon?.windowToken, 0)
    }
}
