package com.studiolexair.preguntascalientes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.studiolexair.preguntascalientes.databinding.ActivitySplashBinding

/**
 * Splash Screen - Pantalla de bienvenida
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashDuration = 2000L // 2 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimations()
        navigateToMenu()
    }

    private fun setupAnimations() {
        // Animación de logo
        binding.logoIcon.apply {
            alpha = 0f
            scaleX = 0.5f
            scaleY = 0.5f
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1000)
                .start()
        }

        binding.appName.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .setStartDelay(300)
                .start()
        }

        binding.developerInfo.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(800)
                .start()
        }

        binding.studioInfo.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(1000)
                .start()
        }
    }

    private fun navigateToMenu() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, splashDuration)
    }
}
