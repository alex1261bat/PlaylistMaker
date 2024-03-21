package com.example.playlistmaker.ui.media.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDataBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.media.PlaylistDataMenuItemAdapter
import com.example.playlistmaker.ui.media.PlaylistDataScreenEvent
import com.example.playlistmaker.ui.media.view_model.PlaylistDataViewModel
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.ui.search.TrackAdapter
import com.example.playlistmaker.util.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDataFragment : Fragment() {
    private var binding: FragmentPlaylistDataBinding? = null
    private val viewModel: PlaylistDataViewModel by viewModel<PlaylistDataViewModel>()
    private lateinit var trackAdapter: TrackAdapter
    private val menuAdapter: PlaylistDataMenuItemAdapter = PlaylistDataMenuItemAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistDataBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBackPressHandling()
        initButtons()
        initTracksBottomSheet()
        initMenuBottomSheet()
        initObservers()
    }

    private fun initBackPressHandling() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isEnabled) {
                    viewModel.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initButtons() {
        binding?.apply {
            toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            btnShare.setOnClickListener { viewModel.shareButtonClick() }
            btnMenu.setOnClickListener { viewModel.menuButtonClick() }
        }
    }

    private fun initTracksBottomSheet() {
        binding?.let {
            BottomSheetBehavior.from(it.bottomSheetTracks).apply {
                addBottomSheetCallback(
                    object : BottomSheetBehavior.BottomSheetCallback() {

                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                        }

                        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                    })
            }
            trackAdapter = TrackAdapter(viewModel::trackClick, viewModel::trackLongClick)
            it.rvTracks.adapter = trackAdapter
        }
    }

    private fun initMenuBottomSheet() {
        binding?.let {
            BottomSheetBehavior.from(it.bottomSheetMenu).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
                addBottomSheetCallback(
                    object : BottomSheetBehavior.BottomSheetCallback() {

                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            it.overlay.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
                        }

                        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                    })
            }
            it.rvMenu.adapter = menuAdapter
        }
    }

    private fun initObservers() {
        viewModel.playlistDataScreenState.observe(viewLifecycleOwner) {
            it.apply {
                setPlaylistData(
                    playlistTitle,
                    playlistDescription,
                    playlistCoverUri,
                    playlistDuration,
                    tracksCount
                )
                setTracksData(tracks)
                setMenuData(playlistCoverUri, playlistTitle, tracksCount)
            }
        }

        viewModel.menuItems.observe(viewLifecycleOwner) { menuAdapter.updateItems(it) }

        viewModel.event.observe(viewLifecycleOwner) {
            when (it) {
                PlaylistDataScreenEvent.NavigateBack -> findNavController().popBackStack()
                is PlaylistDataScreenEvent.SetMenuVisibility -> setMenuVisible(it.isVisible)
                PlaylistDataScreenEvent.ShowMessageNoTracksInPlaylist -> {
                    showMessageNoTracksInPlaylist()
                }

                PlaylistDataScreenEvent.OpenPlayerScreen -> {
                    startActivity(Intent(requireContext(), PlayerActivity()::class.java))
                }

                PlaylistDataScreenEvent.ShowDeleteTrackDialog -> showDeleteTrackDialog()
                is PlaylistDataScreenEvent.ShowDeletePlaylistDialog -> {
                    showDeletePlaylistDialog(it.playlistName)
                }

                is PlaylistDataScreenEvent.NavigateToEditPlaylist -> {
                    navigateToEditPlaylist(it.playlist)
                }
            }
        }
    }

    private fun setPlaylistData(
        title: String,
        description: String,
        playlistCoverUri: String?,
        tracksDuration: Int,
        tracksCount: Int
    ) {
        binding?.apply {
            Glide.with(requireContext())
                .load(playlistCoverUri)
                .into(ivCover)
            tvTitle.text = title
            tvDescription.text = description
            tvTracksInfo.text = getTracksDataText(tracksDuration, tracksCount)
        }
    }

    private fun setTracksData(tracks: List<Track>) {
        trackAdapter.updateData(tracks)
        binding?.apply {
            rvTracks.isVisible = tracks.isNotEmpty()
            lytNoTracks.isVisible = tracks.isEmpty()
        }
    }

    private fun setMenuData(coverUri: String?, name: String, tracksCount: Int) {
        binding?.apply {
            coverUri?.let { ivCoverMenu.load(it) }
            tvNameMenu.text = name
            tvTracksCount.text = buildString {
                append("$tracksCount ")
                append(resources.getQuantityString(R.plurals.track, tracksCount, tracksCount))
            }
        }
    }

    private fun getTracksDataText(tracksDuration: Int, tracksCount: Int): String {
        val tracksDurationText = "$tracksDuration " +
                resources.getQuantityString(R.plurals.minute, tracksDuration, tracksDuration)
        val tracksCountText = "$tracksCount " +
                resources.getQuantityString(R.plurals.track, tracksCount, tracksCount)
        return getString(R.string.playlist_tracks_info, tracksDurationText, tracksCountText)
    }

    private fun navigateToEditPlaylist(playlist: Playlist) {
        val action =
            PlaylistDataFragmentDirections.actionPlaylistInfoFragmentToEditPlaylistFragment(playlist)
        findNavController().navigate(action)
    }

    private fun showDeleteTrackDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_track_dialog_title))
            .setMessage(getString(R.string.delete_track_dialog_message))
            .setNegativeButton(getString(R.string.confirmation_dialog_negative)) { _, _ -> viewModel.deleteTrackCancel() }
            .setPositiveButton(getString(R.string.delete_track_dialog_positive)) { _, _ -> viewModel.deleteTrackConfirm() }
            .show()
    }

    private fun showDeletePlaylistDialog(playlistName: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_playlist_dialog_title))
            .setMessage(getString(R.string.delete_playlist_dialog_message, playlistName))
            .setNegativeButton(getString(R.string.delete_playlist_dialog_negative)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.delete_playlist_dialog_positive)) { _, _ ->
                viewModel.deletePlaylistConfirm()
            }
            .show()
    }

    private fun setMenuVisible(isVisible: Boolean) {
        binding?.bottomSheetMenu?.let { layout ->
            val bottomSheetBehavior = BottomSheetBehavior.from(layout)
            bottomSheetBehavior.state =
                if (isVisible) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun showMessageNoTracksInPlaylist() {
        Toast.makeText(
            requireContext(),
            getString(R.string.no_tracks_in_playlist),
            Toast.LENGTH_SHORT
        ).show()
    }
}
