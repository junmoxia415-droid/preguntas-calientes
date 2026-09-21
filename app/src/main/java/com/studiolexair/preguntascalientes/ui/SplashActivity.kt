package com.studiolexair.preguntascalientes.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.studiolexair.preguntascalientes.audio.SoundManager
import com.studiolexair.preguntascalientes.databinding.ActivitySplashBinding
import com.studiolexair.preguntascalientes.utils.HapticsHelper

/**
 * Splash V2.0 (catálogo §31): logo 🔥 aparece con zoom + partículas +
 * sonido y transición al menú. Menos de 2.5 segundos.
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playIntro()
    }

    private fun playIntro() {
        HapticsHelper.card(this)

        binding.textLogo.scaleX = 0.4f
        binding.textLogo.scaleY = 0.4f
        binding.textLogo.alpha = 0f
        binding.textTitle.alpha = 0f
        binding.textTitle.translationY = 30f
        binding.textSubtitle.alpha = 0f
        binding.textStudio.alpha = 0f

        // 🔥 aparece (zoom + fade)
        binding.textLogo.animate()
            .scaleX(1f).scaleY(1f).alpha(1f)
            .setDuration(650)
            .withEndAction {
                SoundManager.play(this, SoundManager.SFX_POP)
                HapticsHelper.tap(this)
            }
            .start()

        // Título sube
        binding.textTitle.animate()
            .alpha(1f).translationY(0f)
            .setStartDelay(350).setDuration(500)
            .start()

        // "Party Game"
        binding.textSubtitle.animate()
            .alpha(1f)
            .setStartDelay(650).setDuration(400)
            .withEndAction { SoundManager.play(this, SoundManager.SFX_WHOOSH) }
            .start()

        // Créditos del studio
        binding.textStudio.animate()
            .alpha(1f)
            .setStartDelay(950).setDuration(400)
            .start()

        // Pulso continuo del logo
        handler.postDelayed(object : Runnable {
            override fun run() {
                binding.textLogo.animate().scaleX(1.1f).scaleY(1.1f).setDuration(320)
                    .withEndAction {
                        binding.textLogo.animate().scaleX(1f).scaleY(1f).setDuration(320).start()
                    }.start()
                handler.postDelayed(this, 700)
            }
        }, 750)

        handler.postDelayed({ goToMenu() }, 2300)
    }

    private fun goToMenu() {
        SoundManager.playMusic(this, 1)
        startActivity(Intent(this, MenuActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
