package com.example.playlistmaker.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var binding: FragmentSettingsBinding? = null
    private val viewModel: SettingsViewModel by viewModel<SettingsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpButtons()
        setUpThemeSwitcher()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
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
            viewModel.appTheme.observe(viewLifecycleOwner) {
                isChecked = it
            }
            setOnClickListener { viewModel.clickThemeSwitcher(this.isChecked) }
        }
    }
}
