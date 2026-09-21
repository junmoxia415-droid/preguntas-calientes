package com.studiolexair.preguntascalientes.ui

import android.os.Bundle
import com.studiolexair.preguntascalientes.audio.SoundManager
import com.studiolexair.preguntascalientes.databinding.ActivityCreditsBinding

/**
 * Créditos — Identidad Studio Lexair (catálogo §45).
 */
class CreditsActivity : BaseActivity() {

    private lateinit var binding: ActivityCreditsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            SoundManager.click(this)
            finish()
        }

        // Entrada animada
        binding.textVersion.alpha = 0f
        binding.textVersion.animate().alpha(1f).setDuration(600).start()
    }
}
