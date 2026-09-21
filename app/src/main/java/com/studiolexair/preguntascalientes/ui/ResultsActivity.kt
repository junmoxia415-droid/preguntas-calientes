package com.studiolexair.preguntascalientes.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.studiolexair.preguntascalientes.audio.SoundManager
import com.studiolexair.preguntascalientes.databinding.ActivityResultsBinding
import com.studiolexair.preguntascalientes.domain.model.Player
import com.studiolexair.preguntascalientes.utils.HapticsHelper

/**
 * Fin de partida (catálogo §44): ganador con corona, confeti, podio,
 * estadísticas por jugador y botones de rejugar.
 */
class ResultsActivity : BaseActivity() {

    private lateinit var binding: ActivityResultsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindParticles(com.studiolexair.preguntascalientes.R.id.particleBg, "🏆", "🎉", "✨", "🔥")

        @Suppress("DEPRECATION")
        val players: List<Player> = intent.getParcelableArrayListExtra("players") ?: emptyList()
        val achievements = intent.getStringArrayListExtra("new_achievements") ?: arrayListOf()

        if (players.isEmpty()) { finish(); return }

        showWinner(players.first())
        showPodium(players)
        showAchievements(achievements)
        consumeBackPress()

        SoundManager.play(this, SoundManager.SFX_FANFARE)
        HapticsHelper.celebrate(this)
        binding.confettiView.burst(220)

        binding.btnPlayAgain.sfxClick {
            startActivity(Intent(this, PlayersActivity::class.java))
            finish()
        }
        binding.btnMenu.sfxClick {
            startActivity(Intent(this, MenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
            finish()
        }
    }

    private fun showWinner(winner: Player) {
        binding.textWinnerName.text = winner.name
        binding.textWinnerAvatar.text = winner.avatar
        binding.textWinnerXp.text = "⭐ ${winner.points} puntos · ${winner.xp} XP · Nivel ${winner.level}"
        binding.textWinnerName.alpha = 0f
        binding.textWinnerAvatar.scaleX = 0.3f; binding.textWinnerAvatar.scaleY = 0.3f
        binding.textWinnerAvatar.animate().scaleX(1f).scaleY(1f).setDuration(600).start()
        binding.textWinnerName.animate().alpha(1f).setStartDelay(300).setDuration(400).start()
    }

    private fun showPodium(players: List<Player>) {
        val medals = listOf("🥇", "🥈", "🥉")
        binding.layoutPodium.removeAllViews()
        players.forEachIndexed { i, p ->
            val row = layoutInflater.inflate(
                com.studiolexair.preguntascalientes.R.layout.item_result, binding.layoutPodium, false
            ).apply {
                findViewById<android.widget.TextView>(com.studiolexair.preguntascalientes.R.id.textMedal).text =
                    medals.getOrElse(i) { "${i + 1}." }
                findViewById<android.widget.TextView>(com.studiolexair.preguntascalientes.R.id.textName).text =
                    "${p.avatar} ${p.name}"
                findViewById<android.widget.TextView>(com.studiolexair.preguntascalientes.R.id.textScore).text =
                    "${p.points} pts"
                findViewById<android.widget.TextView>(com.studiolexair.preguntascalientes.R.id.textDetail).text =
                    "🔥 racha x${p.maxStreak} · ${p.answered} respuestas · ${p.daresDone} retos"
                alpha = 0f; translationX = -60f
                animate().alpha(1f).translationX(0f).setStartDelay(200L + i * 120).setDuration(350).start()
            }
            binding.layoutPodium.addView(row)
        }
    }

    private fun showAchievements(new: List<String>) {
        if (new.isEmpty()) { binding.cardAchievements.visibility = View.GONE; return }
        binding.cardAchievements.visibility = View.VISIBLE
        binding.textNewAchievements.text = new.joinToString("\n")
    }

    private fun consumeBackPress() {
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { /* pasar por los botones */ }
        })
    }
}
