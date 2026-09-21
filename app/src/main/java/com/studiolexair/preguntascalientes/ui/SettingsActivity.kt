package com.studiolexair.preguntascalientes.ui

import android.os.Bundle
import com.studiolexair.preguntascalientes.utils.PartyDialog
import com.studiolexair.preguntascalientes.utils.PartyDialog.showParty
import com.studiolexair.preguntascalientes.audio.SoundManager
import com.studiolexair.preguntascalientes.databinding.ActivitySettingsBinding
import com.studiolexair.preguntascalientes.utils.HapticsHelper
import com.studiolexair.preguntascalientes.utils.PrefsManager

/**
 * Ajustes (catálogo §13-15, §29, §38): volúmenes, vibración,
 * temporizador por defecto, temas y modo familiar.
 */
class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private var currentTheme = PrefsManager.AppTheme.LOVE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindParticles(com.studiolexair.preguntascalientes.R.id.particleBg, "⚙️", "✨")
        currentTheme = PrefsManager.theme(this)

        setupAudio()
        setupTimer()
        setupTheme()
        setupContentFilter()

        binding.btnBack.sfxClick { finish() }
    }

    private fun setupAudio() {
        binding.sliderMusic.value = PrefsManager.musicVolume(this).toFloat()
        binding.sliderSfx.value = PrefsManager.sfxVolume(this).toFloat()
        binding.textMusicVal.text = "${PrefsManager.musicVolume(this)}%"
        binding.textSfxVal.text = "${PrefsManager.sfxVolume(this)}%"

        binding.sliderMusic.addOnChangeListener { _, v, fromUser ->
            if (fromUser) {
                PrefsManager.setMusicVolume(this, v.toInt())
                binding.textMusicVal.text = "${v.toInt()}%"
                SoundManager.refreshVolumes(this)
                if (v.toInt() == 0) SoundManager.pauseMusic() else SoundManager.resumeMusic(this)
            }
        }
        binding.sliderSfx.addOnChangeListener { _, v, fromUser ->
            if (fromUser) {
                PrefsManager.setSfxVolume(this, v.toInt())
                binding.textSfxVal.text = "${v.toInt()}%"
            }
        }
        binding.sliderSfx.addOnSliderTouchListener(object : com.google.android.material.slider.Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: com.google.android.material.slider.Slider) {}
            override fun onStopTrackingTouch(slider: com.google.android.material.slider.Slider) {
                SoundManager.play(this@SettingsActivity, SoundManager.SFX_DING)
            }
        })

        binding.switchVibration.isChecked = PrefsManager.vibrationEnabled(this)
        binding.switchVibration.setOnCheckedChangeListener { _, on ->
            PrefsManager.setVibration(this, on)
            SoundManager.click(this)
            if (on) HapticsHelper.celebrate(this)
        }
    }

    private fun setupTimer() {
        val timers = listOf(0, 10, 20, 30, 60)
        val chips = listOf(
            binding.chipTimerOff, binding.chipTimer10, binding.chipTimer20,
            binding.chipTimer30, binding.chipTimer60
        )
        val current = PrefsManager.timerSeconds(this)
        chips.forEachIndexed { i, chip ->
            chip.isChecked = timers[i] == current
            chip.setOnClickListener {
                PrefsManager.setTimerSeconds(this, timers[i])
                SoundManager.click(this)
                chips.forEachIndexed { j, c -> c.isChecked = j == i }
            }
        }
    }

    private fun setupTheme() {
        checkTheme(currentTheme)
        listOf(
            binding.radioLove to PrefsManager.AppTheme.LOVE,
            binding.radioMidnight to PrefsManager.AppTheme.MIDNIGHT,
            binding.radioInferno to PrefsManager.AppTheme.INFERNO,
            binding.radioParty to PrefsManager.AppTheme.PARTY,
            binding.radioPremium to PrefsManager.AppTheme.PREMIUM
        ).forEach { (radio, theme) ->
            radio.setOnClickListener {
                SoundManager.play(this, SoundManager.SFX_POP)
                HapticsHelper.card(this)
                PrefsManager.setTheme(this, theme)
                currentTheme = theme
                recreate() // aplicar tema al instante
            }
        }
    }

    private fun checkTheme(theme: PrefsManager.AppTheme) {
        binding.radioLove.isChecked = theme == PrefsManager.AppTheme.LOVE
        binding.radioMidnight.isChecked = theme == PrefsManager.AppTheme.MIDNIGHT
        binding.radioInferno.isChecked = theme == PrefsManager.AppTheme.INFERNO
        binding.radioParty.isChecked = theme == PrefsManager.AppTheme.PARTY
        binding.radioPremium.isChecked = theme == PrefsManager.AppTheme.PREMIUM
    }

    private fun setupContentFilter() {
        binding.switchFamiliMode.isChecked = PrefsManager.familiarMode(this)
        binding.switchFamiliMode.setOnCheckedChangeListener { _, on ->
            SoundManager.click(this)
            if (on) {
                PartyDialog.builder(this)
                    .setTitle("🏡 Modo familiar")
                    .setMessage("Se ocultarán las categorías 🔥 Calientes, 🌶️ Atrevidas y 🎭 Confesiones, y la intensidad máxima será Medio. Perfecto para jugar con cualquiera.")
                    .setPositiveButton("Activar") { _, _ -> PrefsManager.setFamiliarMode(this, true) }
                    .setNegativeButton("Cancelar") { _, _ -> binding.switchFamiliMode.isChecked = false }
                    .setOnCancelListener { binding.switchFamiliMode.isChecked = false }
                    .showParty()
            } else {
                PrefsManager.setFamiliarMode(this, false)
            }
        }
    }
}
