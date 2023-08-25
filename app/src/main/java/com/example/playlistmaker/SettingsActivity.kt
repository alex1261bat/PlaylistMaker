package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<androidx.appcompat.widget.Toolbar>(R.id.settings_back_button)
        val shareButton = findViewById<FrameLayout>(R.id.share_button)
        val supportButton = findViewById<FrameLayout>(R.id.sendToSupport_button)
        val userAgreementButton = findViewById<FrameLayout>(R.id.userAgreement_button)

        backButton.setOnClickListener {
            finish()
        }

        shareButton.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND)
            val message = "https://practicum.yandex.ru/android-developer/"
            sendIntent.putExtra(Intent.EXTRA_TEXT, message)
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            val message = "Спасибо разработчикам и разработчицам за крутое приложение!"
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("alex126.1bat@gmail.com"))
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            supportIntent.putExtra(Intent.EXTRA_SUBJECT,
                "Тема: Сообщение разработчикам и разработчицам приложения Playlist Maker")
            startActivity(supportIntent)
        }

        userAgreementButton.setOnClickListener {
            val userAgreementIntent = Intent(Intent.ACTION_VIEW)
            userAgreementIntent.data = Uri.parse("https://yandex.ru/legal/practicum_offer/")
            startActivity(userAgreementIntent)
        }
    }
}
