package com.example.playlistmaker.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private var binding: ActivitySettingsBinding? = null
    private var viewModel: SettingsViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        viewModel = ViewModelProvider(this).get<SettingsViewModel>()

        binding?.settingsBackButton?.setOnClickListener {
            finish()
        }

        setUpThemeSwitcher()
        setUpButtons()
    }

    private fun setUpButtons() {
        binding?.apply {
            shareButton.setOnClickListener { viewModel?.clickShareButton() }
            sendToSupportButton.setOnClickListener { viewModel?.clickSendToSupportButton() }
            userAgreementButton.setOnClickListener { viewModel?.clickUserAgreementButton() }
        }
    }

    private fun setUpThemeSwitcher() {
        binding?.themeSwitcher?.apply {
            viewModel?.appTheme?.observe(this@SettingsActivity) {
                isChecked = it
            }
            setOnClickListener { viewModel?.clickThemeSwitcher(this.isChecked) }
        }
    }
}
