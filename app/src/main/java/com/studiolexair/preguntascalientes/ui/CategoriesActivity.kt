package com.studiolexair.preguntascalientes.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.studiolexair.preguntascalientes.utils.PartyDialog
import com.studiolexair.preguntascalientes.utils.PartyDialog.showParty
import com.studiolexair.preguntascalientes.audio.SoundManager
import com.studiolexair.preguntascalientes.databinding.ActivityCategoriesBinding
import com.studiolexair.preguntascalientes.domain.model.Category
import com.studiolexair.preguntascalientes.utils.GameSession
import com.studiolexair.preguntascalientes.utils.HapticsHelper
import com.studiolexair.preguntascalientes.utils.PrefsManager

/**
 * Configuración de partida (categorías + intensidad + temporizador §15).
 * Respeta el modo familiar (§38) y confirma la intensidad extrema.
 */
class CategoriesActivity : BaseActivity() {

    private lateinit var binding: ActivityCategoriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindParticles(com.studiolexair.preguntascalientes.R.id.particleBg, "🎯", "✨", "🔥")

        binding.textModeBadge.text = "${GameSession.mode.title()} · ${GameSession.players.size} jugadores"

        setupChips()
        setupIntensity()
        setupTimer()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnStartGame.sfxClick { tryStart() }
    }

    private fun availableCategories(): List<Category> =
        Category.visible(PrefsManager.familiarMode(this))

    private fun setupChips() {
        val familiar = PrefsManager.familiarMode(this)
        if (familiar) {
            binding.textFamiliarNote.text = "🏡 Modo familiar activo: categorías 🔥🌶️🎭 ocultas y sin intensidad extrema"
            binding.textFamiliarNote.visibility = android.view.View.VISIBLE
        }
        val chipMap = mapOf(
            Category.CALIENTES to binding.chipCalientes,
            Category.INTERESANTES to binding.chipInteresantes,
            Category.DIVERTIDAS to binding.chipDivertidas,
            Category.ATREVIDAS to binding.chipAtrevidas,
            Category.ROMANTICAS to binding.chipRomanticas,
            Category.CONFESIONES to binding.chipConfesiones,
            Category.PAREJAS to binding.chipParejas,
            Category.FIESTA to binding.chipFiesta
        )
        val visible = availableCategories()
        chipMap.forEach { (cat, chip) ->
            if (!visible.contains(cat)) {
                chip.visibility = android.view.View.GONE
            } else {
                chip.isChecked = GameSession.selectedCategories.contains(cat)
                chip.setOnCheckedChangeListener { _, checked ->
                    SoundManager.click(this)
                    HapticsHelper.tap(this)
                    if (checked) GameSession.selectedCategories.add(cat)
                    else GameSession.selectedCategories.remove(cat)
                }
            }
        }
        // Limpiar las seleccionadas que ahora estén ocultas
        GameSession.selectedCategories.retainAll(visible.toSet())
    }

    private fun setupIntensity() {
        binding.sliderIntensity.value = GameSession.intensity.toFloat()
        binding.sliderIntensity.addOnChangeListener { _, value, _ ->
            GameSession.intensity = value.toInt().coerceIn(1, 3)
            HapticsHelper.tap(this)
            SoundManager.play(this, SoundManager.SFX_TICK)
        }
        binding.sliderIntensity.setLabelFormatter { value ->
            when (value.toInt()) { 1 -> "😊 Suave"; 2 -> "😏 Medio"; else -> "🔥 Extremo" }
        }
        if (PrefsManager.familiarMode(this)) {
            binding.sliderIntensity.valueTo = 2f
            if (GameSession.intensity > 2) { GameSession.intensity = 2; binding.sliderIntensity.value = 2f }
        }
    }

    private fun setupTimer() {
        val timers = listOf(0, 10, 20, 30, 60)
        val chips = listOf(
            binding.chipTimerOff, binding.chipTimer10, binding.chipTimer20,
            binding.chipTimer30, binding.chipTimer60
        )
        GameSession.timerSeconds = PrefsManager.timerSeconds(this)
        chips.forEachIndexed { i, chip ->
            chip.isChecked = GameSession.timerSeconds == timers[i]
            chip.setOnClickListener {
                GameSession.timerSeconds = timers[i]
                PrefsManager.setTimerSeconds(this, timers[i])
                SoundManager.click(this)
                chips.forEachIndexed { j, c -> c.isChecked = j == i }
            }
        }
    }

    private fun tryStart() {
        if (GameSession.selectedCategories.isEmpty()) {
            Toast.makeText(this, "Elige al menos una categoría 🎯", Toast.LENGTH_SHORT).show()
            HapticsHelper.fail(this)
            return
        }
        // §38: confirmación de intensidad extrema
        if (GameSession.intensity == 3) {
            PartyDialog.builder(this)
                .setTitle("🔥 Intensidad EXTREMA")
                .setMessage("El contenido será muy atrevido. Todo el grupo debe estar de acuerdo y siempre puedes usar PASAR sin coste.\n\n¿Continuamos?")
                .setPositiveButton("🔥 A por ello") { _, _ -> launchGame() }
                .setNegativeButton("Bajar a Medio 😏") { _, _ ->
                    GameSession.intensity = 2
                    binding.sliderIntensity.value = 2f
                    launchGame()
                }
                .showParty()
        } else launchGame()
    }

    private fun launchGame() {
        SoundManager.play(this, SoundManager.SFX_GO)
        HapticsHelper.event(this)
        startActivity(Intent(this, GameActivity::class.java))
    }
}
