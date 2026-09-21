package com.studiolexair.preguntascalientes.ui

import android.content.Intent
import android.os.Bundle
import com.studiolexair.preguntascalientes.utils.PartyDialog
import com.studiolexair.preguntascalientes.utils.PartyDialog.showParty
import com.studiolexair.preguntascalientes.audio.SoundManager
import com.studiolexair.preguntascalientes.databinding.ActivityRouletteBinding
import com.studiolexair.preguntascalientes.domain.model.Category
import com.studiolexair.preguntascalientes.domain.model.GameMode
import com.studiolexair.preguntascalientes.utils.GameSession
import com.studiolexair.preguntascalientes.utils.HapticsHelper
import com.studiolexair.preguntascalientes.utils.PrefsManager

/**
 * Ruleta 🎡 (catálogo §24): gira y decide el modo de la partida.
 */
class RouletteActivity : BaseActivity() {

    private lateinit var binding: ActivityRouletteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouletteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindParticles(com.studiolexair.preguntascalientes.R.id.particleBg, "🎡", "✨", "🎲")

        val modes = GameMode.values().filter { it != GameMode.ALEATORIO }
        binding.rouletteView.labels = modes.map { it.emoji to it.displayName }

        binding.btnSpin.sfxClick {
            if (binding.rouletteView.isSpinning) return@sfxClick
            HapticsHelper.event(this)
            binding.textResult.text = "🎡 Girando..."
            tickWhileSpinning()
            binding.rouletteView.spin { index ->
                val mode = modes[index]
                SoundManager.play(this, SoundManager.SFX_FANFARE)
                HapticsHelper.celebrate(this)
                binding.confettiView.burst(120)
                binding.textResult.text = mode.title()
                binding.root.postDelayed({ confirmMode(mode) }, 900)
            }
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private var ticking = true
    private fun tickWhileSpinning() {
        ticking = true
        binding.rouletteView.postDelayed(object : Runnable {
            override fun run() {
                if (!binding.rouletteView.isSpinning || !ticking) return
                SoundManager.play(this@RouletteActivity, SoundManager.SFX_TICK)
                HapticsHelper.tick(this@RouletteActivity)
                binding.rouletteView.postDelayed(this, 220)
            }
        }, 220)
    }

    private fun confirmMode(mode: GameMode) {
        ticking = false
        PartyDialog.builder(this)
            .setTitle(mode.title())
            .setMessage("${mode.description}\n\n¿Jugamos en este modo?")
            .setPositiveButton("🔥 ¡Vamos!") { _, _ ->
                GameSession.mode = mode
                GameSession.intensity = (2..3).random()
                val cats = Category.visible(PrefsManager.familiarMode(this))
                GameSession.selectedCategories = cats.shuffled().take(5).toMutableSet()
                startActivity(Intent(this, PlayersActivity::class.java))
                finish()
            }
            .setNegativeButton("🎡 Girar otra vez", null)
            .setOnCancelListener { binding.textResult.text = "" }
            .showParty()
    }

    override fun onStop() { ticking = false; super.onStop() }
}
