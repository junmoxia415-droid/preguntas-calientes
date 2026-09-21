package com.studiolexair.preguntascalientes

import android.app.Application
import com.studiolexair.preguntascalientes.audio.SoundManager
import com.studiolexair.preguntascalientes.data.db.DatabaseProvider

/**
 * Application: inicializa Room y el sistema de sonido.
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
class PreguntasApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseProvider.get(this)   // calentar la DB
        SoundManager.init(this)
    }
}
