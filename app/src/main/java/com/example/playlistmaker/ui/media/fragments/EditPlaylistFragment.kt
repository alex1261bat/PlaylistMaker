package com.example.playlistmaker.ui.media.fragments

import android.os.Bundle
import android.view.View
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.media.view_model.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : NewPlaylistFragment() {

    override val viewModel: EditPlaylistViewModel by viewModel<EditPlaylistViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.editPlaylist.observe(viewLifecycleOwner) { playlist ->
            binding?.apply {
                editTextName.setText(playlist.title)
                editTextDescription.setText(playlist.description)
            }
        }
    }

    override fun initToolbar() {
        super.initToolbar()
        binding?.apply { toolbar.setTitle(R.string.edit) }
    }

    override fun initCompleteButton() {
        super.initCompleteButton()
        binding?.apply { btnComplete.setText(R.string.save) }
    }
}
