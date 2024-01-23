package com.example.playlistmaker.ui.media

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaActivity : AppCompatActivity() {
    private var binding: ActivityMediaBinding? = null
    private var tabMediator: TabLayoutMediator? = null
    private val tabTitleIds = arrayOf(
        R.string.favourite_tracks,
        R.string.playlist
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.mediaBackButton?.setNavigationOnClickListener { finish() }
        initTabLayout()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator?.detach()
    }

    private fun initTabLayout() = binding?.run {
        viewPager.adapter = MediaAdapter(supportFragmentManager, lifecycle)
        tabMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getString(tabTitleIds[position])
        }
        tabMediator?.attach()
    }
}
