package com.example.playlistmaker.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private var binding: ActivitySettingsBinding? = null
    private val viewModel: SettingsViewModel by viewModel<SettingsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.tbSettingsBack?.setOnClickListener {
            finish()
        }

        setUpThemeSwitcher()
        setUpButtons()
    }

    private fun setUpButtons() {
        binding?.apply {
            flShare.setOnClickListener { viewModel.clickShareButton() }
            flSendToSupport.setOnClickListener { viewModel.clickSendToSupportButton() }
            flUserAgreement.setOnClickListener { viewModel.clickUserAgreementButton() }
        }
    }

    private fun setUpThemeSwitcher() {
        binding?.smThemeSwitcher?.apply {
            viewModel.appTheme.observe(this@SettingsActivity) {
                isChecked = it
            }
            setOnClickListener { viewModel.clickThemeSwitcher(this.isChecked) }
        }
    }
}
