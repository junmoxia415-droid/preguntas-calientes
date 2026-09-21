package com.studiolexair.preguntascalientes.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.studiolexair.preguntascalientes.audio.SoundManager
import com.studiolexair.preguntascalientes.utils.HapticsHelper
import com.studiolexair.preguntascalientes.utils.PrefsManager
import com.studiolexair.preguntascalientes.utils.views.ParticleView

/**
 * BaseActivity V2.0: aplica el tema guardado (🌸🌙🔥🎉💎) antes de
 * inflar la UI y ofrece atajos de sonido/haptics/partículas.
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Aplicar tema propio antes de super.onCreate (§29)
        setTheme(PrefsManager.theme(this).styleRes)
        super.onCreate(savedInstanceState)
        // V2.1: plantilla persistente de jugadores disponible en toda la app
        com.studiolexair.preguntascalientes.utils.GameSession.ensureRosterLoaded(this)
    }

    /** Click con sonido + haptic. Usar en botones principales. */
    fun View.sfxClick(action: () -> Unit) {
        setOnClickListener {
            SoundManager.click(this@BaseActivity)
            HapticsHelper.tap(this@BaseActivity)
            it.animPulse()
            action()
        }
    }

    fun View.animPulse() {
        animate().scaleX(0.94f).scaleY(0.94f).setDuration(70).withEndAction {
            animate().scaleX(1f).scaleY(1f).setDuration(120).start()
        }.start()
    }

    /** Activa partículas con emojis de modo si el layout incluye particleBg. */
    fun bindParticles(id: Int, vararg modeEmoji: String) {
        (findViewById<View>(id) as? ParticleView)?.setModeEmoji(*modeEmoji)
    }

    override fun onResume() {
        super.onResume()
        SoundManager.refreshVolumes(this)
    }
}
