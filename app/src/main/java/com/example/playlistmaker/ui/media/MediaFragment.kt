package com.example.playlistmaker.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaFragment : Fragment() {

    private var binding: FragmentMediaBinding? = null

    private var tabMediator: TabLayoutMediator? = null
    private val tabsTitleIds = arrayOf(
        R.string.favourite_tracks,
        R.string.playlist
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTabLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator?.detach()
    }

    private fun initTabLayout() = binding?.run {
        vpMedia.adapter = MediaAdapter(childFragmentManager, lifecycle)
        tabMediator = TabLayoutMediator(tlMediaTab, vpMedia) { tab, position ->
            tab.text = getString(tabsTitleIds[position])
        }
        tabMediator?.attach()
    }
}
