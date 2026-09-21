package com.studiolexair.preguntascalientes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.studiolexair.preguntascalientes.databinding.ActivityCreditsBinding

/**
 * Pantalla de créditos
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
class CreditsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreditsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()
        setupAnimations()
    }

    private fun setupUI() {
        binding.apply {
            textAppVersion.text = "Versión: ${packageManager.getPackageInfo(packageName, 0).versionName}"
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupAnimations() {
        val views = listOf(
            binding.cardHeader,
            binding.cardDeveloper,
            binding.cardStudio,
            binding.cardInfo,
            binding.cardThanks
        )

        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 100f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay((index * 120).toLong())
                .start()
        }

        binding.logoCredits.apply {
            alpha = 0f
            scaleX = 0.5f
            scaleY = 0.5f
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .start()
        }
    }
}
