package com.example.playlistmaker.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.model.App

class SettingsActivity : AppCompatActivity() {
    private var binding: ActivitySettingsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val backButton = binding?.settingsBackButton
        val shareButton = binding?.shareButton
        val supportButton = binding?.sendToSupportButton
        val userAgreementButton = binding?.userAgreementButton
        val themeSwitcher = binding?.themeSwitcher
        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        themeSwitcher?.isChecked = (applicationContext as App).darkTheme

        themeSwitcher?.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPrefs.edit()
                .putString(DARK_THEME_KEY, checked.toString())
                .apply()
        }

        backButton?.setOnClickListener {
            finish()
        }

        shareButton?.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = getString(R.string.share_intent_type)
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
            startActivity(Intent.createChooser(shareIntent, null))
        }

        supportButton?.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse(getString(R.string.mail_to))
            supportIntent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.email))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_theme))
            startActivity(supportIntent)
        }

        userAgreementButton?.setOnClickListener {
            val userAgreementIntent = Intent(Intent.ACTION_VIEW)
            userAgreementIntent.data = Uri.parse(getString(R.string.user_agreement_data))
            startActivity(userAgreementIntent)
        }
    }
}
