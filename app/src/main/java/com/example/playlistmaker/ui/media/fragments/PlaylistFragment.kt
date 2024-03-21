package com.example.playlistmaker.ui.media.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.ui.media.PlaylistAdapter
import com.example.playlistmaker.ui.media.PlaylistScreenEvent
import com.example.playlistmaker.ui.media.view_model.PlaylistViewModel
import com.example.playlistmaker.util.ResultKeyHolder
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {
    private var binding: FragmentPlaylistBinding? = null
    private val viewModel: PlaylistViewModel by viewModel<PlaylistViewModel>()
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(layoutInflater)
        requireActivity().supportFragmentManager.setFragmentResultListener(
            ResultKeyHolder.KEY_PLAYLIST_CREATED,
            viewLifecycleOwner
        ) { _, bundle ->
            bundle.getString(ResultKeyHolder.KEY_PLAYLIST_TITLE)?.let { showPlaylistCreatedMessage(it) }
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initPlaylistsRecycler()
        binding?.ivNewPlaylist?.setOnClickListener { viewModel.newPlaylistButtonClick() }
        binding?.rvPlaylists?.adapter = playlistAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun initObservers() {
        viewModel.state.observe(viewLifecycleOwner) {
            playlistAdapter.submitList(it)
            binding?.llEmptyPlaylist?.isVisible = it.isEmpty()
            binding?.rvPlaylists?.isVisible = it.isNotEmpty()
        }

        viewModel.playlistEvent.observe(viewLifecycleOwner) {
            when (it) {
                PlaylistScreenEvent.NavigateToNewPlaylist -> navigateToNewPlaylist()
                is PlaylistScreenEvent.NavigateToPlaylistData -> navigateToPlaylistData(it.playlistId)
            }
        }
    }

    private fun navigateToNewPlaylist() {
        findNavController()
            .navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
    }

    private fun navigateToPlaylistData(playlistId: String) {
        val action = MediaFragmentDirections.actionMediaFragmentToPlaylistDataFragment(playlistId)
        findNavController().navigate(action)
    }

    private fun showPlaylistCreatedMessage(playlistName: String) {
        Toast.makeText(requireContext(), getString(R.string.playlist_created_snackbar, playlistName),
            Toast.LENGTH_SHORT)
            .show()
    }

    private fun initPlaylistsRecycler() {
        playlistAdapter = PlaylistAdapter(viewModel::playlistClicked)
        binding?.rvPlaylists?.adapter = playlistAdapter
    }
}
