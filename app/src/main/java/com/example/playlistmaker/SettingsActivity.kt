package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backArrowButton = findViewById<ImageButton>(R.id.back_arrow)

        backArrowButton.setOnClickListener {
            val backArrowButtonIntent = Intent(this, MainActivity::class.java)
            startActivity(backArrowButtonIntent)
        }
    }
}