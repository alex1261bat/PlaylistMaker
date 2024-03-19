package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.player.model.MediaPlayerStatus
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.TrackViewHolder
import com.example.playlistmaker.util.ResultKeyHolder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {

    private var binding: FragmentPlayerBinding? = null
    private val viewModel: PlayerViewModel by viewModel<PlayerViewModel>()
    private var playlistAdapter: BottomSheetPlaylistAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(layoutInflater)
        requireActivity().supportFragmentManager.setFragmentResultListener(
            ResultKeyHolder.KEY_PLAYLIST_CREATED,
            viewLifecycleOwner
        ) { _, bundle ->
            bundle.getString(ResultKeyHolder.KEY_PLAYLIST_TITLE)?.let {
                val text = getString(R.string.playlist_created_snackbar, it)
                showToast(text)
            }
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPlaylistsBottomSheet()
        initViews()
        initObservers()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun initViews(){
        binding?.apply {
            tbPlayerBack.setNavigationOnClickListener { requireActivity().finish() }
            ibPlay.setOnClickListener { viewModel.clickPlay() }
            ibLike.setOnClickListener { viewModel.likeButtonClick() }
            ibAddToPlaylist.setOnClickListener { viewModel.addButtonClick() }
            btnCreatePlaylist.setOnClickListener { viewModel.createPlaylistButtonClick() }
        }
    }

    private fun initObservers() {
        viewModel.state.observe(viewLifecycleOwner) {
            it.track?.let { track -> bindTrack(track) }
            if (it.track != null) {
                binding?.tvTimer?.text = it.trackTime.ifEmpty { getString(R.string.timer_start_value_00_00) }
            }

            it?.playerState?.let { state ->
                when (state) {
                    MediaPlayerStatus.STATE_PLAYING ->
                        binding?.ibPlay?.setImageResource(R.drawable.button_pause)

                    MediaPlayerStatus.STATE_PREPARED,
                    MediaPlayerStatus.STATE_PAUSED ->
                        binding?.ibPlay?.setImageResource(R.drawable.button_play)
                }
            }

            val btnLikeResource = if (it.isFavorite) R.drawable.button_like
            else R.drawable.btn_like_not_active
            binding?.ibLike?.setImageResource(btnLikeResource)
        }

        viewModel.playlists.observe(viewLifecycleOwner) { playlistAdapter?.submitList(it) }

        viewModel.event.observe(viewLifecycleOwner) {
            when (it) {
                is PlayerScreenEvent.OpenPlaylistsBottomSheet -> {
                    binding?.bottomSheetPlaylists?.let {
                        val bottomSheetBehavior = BottomSheetBehavior.from(it)
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }

                is PlayerScreenEvent.ShowAddedTrackMessage -> {
                    showToast(getString(R.string.added_to_playlist, it.playlistTitle))
                }

                is PlayerScreenEvent.ShowTrackAlreadyInPlaylistMessage -> {
                    showToast(getString(R.string.track_already_in_playlist, it.playlistTitle))
                }

                is PlayerScreenEvent.ClosePlaylistsBottomSheet -> {
                    binding?.bottomSheetPlaylists?.let {
                        val bottomSheetBehavior = BottomSheetBehavior.from(it)
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }

                is PlayerScreenEvent.NavigateToCreatePlaylistFragment -> {
                    findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
                }
            }
        }
    }

    private fun bindTrack(track: Track) {
        binding?.tvTrackName?.text = track.trackName
        binding?.tvArtistName?.text = track.artistName
        binding?.tvTrackTime?.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTime.toLong()).orEmpty()

        binding?.tvTrackAlbum?.text = track.collectionName.orEmpty()
        binding?.tvTrackYear?.text = track.releaseDate?.substring(0, 4).orEmpty()

        binding?.tvTrackGenre?.text = track.primaryGenreName
        binding?.tvTrackCountry?.text = track.country

        if (track.artworkUrl100.isNotEmpty()) {
            binding?.ivTrackImage?.setImageResource(R.drawable.track_image_placeholder)

            Glide.with(binding?.ivTrackImage!!)
                .load(track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
                .placeholder(R.drawable.track_image_placeholder)
                .centerCrop()
                .transform(RoundedCorners(TrackViewHolder.ROUNDING_RADIUS))
                .into(binding?.ivTrackImage!!)
        }
    }

    private fun initPlaylistsBottomSheet() {
        binding?.let {
            BottomSheetBehavior.from(it.bottomSheetPlaylists).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
                addBottomSheetCallback(
                    object : BottomSheetBehavior.BottomSheetCallback() {

                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            it.overlay.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
                        }

                        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                    })
            }
            playlistAdapter = BottomSheetPlaylistAdapter(viewModel::playlistClick)
            it.btmRvPlaylists.adapter = playlistAdapter
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}
