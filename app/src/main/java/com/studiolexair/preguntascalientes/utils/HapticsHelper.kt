package com.studiolexair.preguntascalientes.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Haptic feedback (catálogo §14): la app se siente como un juego.
 */
object HapticsHelper {

    private fun vibrator(context: Context): Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
                .defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    } catch (e: Exception) { null }

    private fun vibrate(context: Context, ms: Long, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
        if (!PrefsManager.vibrationEnabled(context)) return
        val v = vibrator(context) ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(ms, amplitude))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(ms)
        }
    }

    private fun pattern(context: Context, timings: LongArray) {
        if (!PrefsManager.vibrationEnabled(context)) return
        val v = vibrator(context) ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createWaveform(timings, -1))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(timings, -1)
        }
    }

    /** Toque de botón → pulsación muy corta. */
    fun tap(context: Context) = vibrate(context, 18, 80)
    /** Carta/flip → pulsación corta. */
    fun card(context: Context) = vibrate(context, 40, 140)
    /** Evento especial → vibración fuerte. */
    fun event(context: Context) = vibrate(context, 120, 255)
    /** Tick de cuenta regresiva. */
    fun tick(context: Context) = vibrate(context, 25, 110)
    /** Logro/victoria → patrón de celebración. */
    fun celebrate(context: Context) = pattern(context, longArrayOf(0, 60, 60, 80, 60, 160))
    /** Error/fallo. */
    fun fail(context: Context) = pattern(context, longArrayOf(0, 90, 50, 40))
}
