package com.studiolexair.preguntascalientes.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.studiolexair.preguntascalientes.audio.SoundManager
import com.studiolexair.preguntascalientes.data.db.SessionRepository
import com.studiolexair.preguntascalientes.databinding.ActivityMenuBinding
import com.studiolexair.preguntascalientes.utils.GameSession
import com.studiolexair.preguntascalientes.utils.HapticsHelper
import kotlinx.coroutines.launch

/**
 * Menú principal V2.0 (catálogo §32): JUGAR / MODOS / COLECCIÓN /
 * LOGROS / ESTADÍSTICAS / AJUSTES + Continuar partida (§26).
 */
class MenuActivity : BaseActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindParticles(com.studiolexair.preguntascalientes.R.id.particleBg, "🔥", "❤️", "✨", "💫")
        animateEntry()
        setupButtons()
        setupBackHandling()
    }

    private fun animateEntry() {
        val views = listOf(binding.textTitle, binding.textReady, binding.btnPlay,
            binding.cardModes, binding.cardCollection, binding.cardAchievements,
            binding.cardStats, binding.rowBottom)
        views.forEachIndexed { i, v ->
            v.alpha = 0f; v.translationY = 40f
            v.animate().alpha(1f).translationY(0f)
                .setStartDelay(80L * i).setDuration(350).start()
        }
        binding.textReady.alpha = 0f
        binding.textReady.animate().alpha(1f).setStartDelay(150).setDuration(500).withEndAction {
            binding.textReady.animate().scaleX(1.08f).scaleY(1.08f).setDuration(420).withEndAction {
                binding.textReady.animate().scaleX(1f).scaleY(1f).setDuration(420).start()
            }.start()
        }.start()
    }

    private fun setupButtons() {
        binding.btnPlay.sfxClick {
            GameSession.reset()
            startActivity(Intent(this, PlayersActivity::class.java))
        }
        binding.btnContinue.sfxClick {
            HapticsHelper.event(this)
            startActivity(Intent(this, GameActivity::class.java).putExtra("continue", true))
        }
        binding.cardModes.sfxClick { startActivity(Intent(this, ModesActivity::class.java)) }
        binding.cardCollection.sfxClick { startActivity(Intent(this, CollectionActivity::class.java)) }
        binding.cardAchievements.sfxClick { startActivity(Intent(this, AchievementsActivity::class.java)) }
        binding.cardStats.sfxClick { startActivity(Intent(this, StatsActivity::class.java)) }
        binding.btnSettings.sfxClick { startActivity(Intent(this, SettingsActivity::class.java)) }
        binding.btnCustom.sfxClick { startActivity(Intent(this, CustomQuestionsActivity::class.java)) }
        binding.btnCredits.sfxClick { startActivity(Intent(this, CreditsActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        SoundManager.playMusic(this, 1)
        // §26: mostrar "Continuar partida" si hay una guardada
        lifecycleScope.launch {
            val summary = SessionRepository.summary(this@MenuActivity)
            binding.btnContinue.visibility = if (summary != null) View.VISIBLE else View.GONE
            binding.textContinueInfo.text = summary ?: ""
            binding.textContinueInfo.visibility = if (summary != null) View.VISIBLE else View.GONE
        }
    }

    private fun setupBackHandling() {
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@MenuActivity)
                    .setTitle("🔥 Salir de Preguntas Calientes")
                    .setMessage("¿Seguro que quieres salir? ¡La fiesta te espera!")
                    .setPositiveButton("Salir") { _, _ -> finishAffinity() }
                    .setNegativeButton("Seguir jugando", null)
                    .show()
            }
        })
    }
}
