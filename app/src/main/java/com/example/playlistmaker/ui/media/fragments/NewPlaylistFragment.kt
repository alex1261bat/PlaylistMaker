package com.example.playlistmaker.ui.media.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.ui.media.NewPlaylistEvent
import com.example.playlistmaker.ui.media.view_model.NewPlaylistViewModel
import com.example.playlistmaker.util.ResultKeyHolder
import com.example.playlistmaker.util.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewPlaylistFragment : Fragment() {

    private var binding: FragmentNewPlaylistBinding? = null
    private val viewModel: NewPlaylistViewModel by viewModel<NewPlaylistViewModel>()
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        it?.let { uri -> viewModel.playlistCoverSelected(uri) }
    }

    private companion object {
        const val KEY_PLAYLIST_COVER_URI = "key_playlist_cover_uri"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPlaylistBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playlistCoverUri = savedInstanceState?.getParcelable<Uri>(KEY_PLAYLIST_COVER_URI)
        playlistCoverUri?.let { viewModel.playlistCoverSelected(it) }
        binding?.toolbar?.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        setupTextListeners()
        setupClickListeners()
        setupBackPressHandling()
        initObservers()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_PLAYLIST_COVER_URI, viewModel.playlistCover.value)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupTextListeners() {
        binding?.apply {
            editTextName.doAfterTextChanged { viewModel.playlistTitleChanged(it.toString()) }
            editTextDescription.doAfterTextChanged {
                viewModel.playlistDescriptionChanged(it.toString())
            }
        }
    }

    private fun setupClickListeners() {
        binding?.apply {
            ibCover.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            btnCreate.setOnClickListener { viewModel.createPlaylistClick() }
        }
    }

    private fun setupBackPressHandling() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isEnabled) {
                    viewModel.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initObservers() {
        viewModel.buttonCreateEnabled.observe(viewLifecycleOwner) {
            binding?.btnCreate?.isEnabled = it
        }

        viewModel.playlistCover.observe(viewLifecycleOwner) {
            it?.let { setSelectedCover(it) }
        }

        viewModel.playlistEvent.observe(viewLifecycleOwner) {
            when (it) {
                is NewPlaylistEvent.NavigateBack -> findNavController().popBackStack()
                is NewPlaylistEvent.ShowBackConfirmationDialog -> showBackConfirmationDialog()
                is NewPlaylistEvent.SetPlaylistCreatedResult -> {
                    requireActivity().supportFragmentManager.setFragmentResult(
                        ResultKeyHolder.KEY_PLAYLIST_CREATED,
                        bundleOf(ResultKeyHolder.KEY_PLAYLIST_TITLE to it.playlistTitle)
                    )
                }
            }
        }
    }

    private fun showBackConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.confirmation_dialog_title))
            .setMessage(getString(R.string.confirmation_dialog_message))
            .setNegativeButton(getString(
                R.string.confirmation_dialog_negative)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(
                R.string.confirmation_dialog_positive)) { _, _ -> viewModel.backPressConfirmed() }
            .show()
    }

    private fun setSelectedCover(uri: Uri) {
        binding?.ibCover?.load(uri.toString())
    }
}
