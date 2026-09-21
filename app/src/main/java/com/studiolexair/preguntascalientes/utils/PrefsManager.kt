package com.studiolexair.preguntascalientes.utils

import android.content.Context
import android.content.SharedPreferences
import com.studiolexair.preguntascalientes.R

/**
 * Preferencias de la app: volúmenes, vibración, temporizador, tema,
 * modo familiar (filtro de contenido §38) y progreso de colección.
 */
object PrefsManager {

    private const val PREFS = "preguntas_calientes_prefs"
    private const val KEY_MUSIC_VOL = "music_volume"        // 0..100
    private const val KEY_SFX_VOL = "sfx_volume"            // 0..100
    private const val KEY_VIBRATION = "vibration"
    private const val KEY_TIMER = "timer_seconds"           // 0/10/20/30/60
    private const val KEY_FAMILIAR = "modo_familiar"
    private const val KEY_THEME = "theme_key"
    private const val KEY_DISCOVERED = "discovered_ids"

    enum class AppTheme(val key: String, val label: String, val styleRes: Int) {
        LOVE("love", "🌸 Love", R.style.Theme_PreguntasCalientes_Love),
        MIDNIGHT("midnight", "🌙 Midnight", R.style.Theme_PreguntasCalientes_Midnight),
        INFERNO("inferno", "🔥 Inferno", R.style.Theme_PreguntasCalientes_Inferno),
        PARTY("party", "🎉 Party", R.style.Theme_PreguntasCalientes_Party),
        PREMIUM("premium", "💎 Premium", R.style.Theme_PreguntasCalientes_Premium);

        companion object {
            fun fromKey(key: String?): AppTheme = values().find { it.key == key } ?: LOVE
        }
    }

    private fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    // ── Audio ──
    fun musicVolume(context: Context): Int = prefs(context).getInt(KEY_MUSIC_VOL, 55)
    fun setMusicVolume(context: Context, v: Int) = prefs(context).edit().putInt(KEY_MUSIC_VOL, v).apply()
    fun sfxVolume(context: Context): Int = prefs(context).getInt(KEY_SFX_VOL, 80)
    fun setSfxVolume(context: Context, v: Int) = prefs(context).edit().putInt(KEY_SFX_VOL, v).apply()

    // ── Haptics ──
    fun vibrationEnabled(context: Context): Boolean = prefs(context).getBoolean(KEY_VIBRATION, true)
    fun setVibration(context: Context, on: Boolean) = prefs(context).edit().putBoolean(KEY_VIBRATION, on).apply()

    // ── Timer ──
    fun timerSeconds(context: Context): Int = prefs(context).getInt(KEY_TIMER, 0)
    fun setTimerSeconds(context: Context, s: Int) = prefs(context).edit().putInt(KEY_TIMER, s).apply()

    // ── Filtro de contenido ──
    fun familiarMode(context: Context): Boolean = prefs(context).getBoolean(KEY_FAMILIAR, false)
    fun setFamiliarMode(context: Context, on: Boolean) = prefs(context).edit().putBoolean(KEY_FAMILIAR, on).apply()

    // ── Tema ──
    fun theme(context: Context): AppTheme = AppTheme.fromKey(prefs(context).getString(KEY_THEME, null))
    fun setTheme(context: Context, theme: AppTheme) =
        prefs(context).edit().putString(KEY_THEME, theme.key).apply()

    // ── Colección ──
    fun getDiscoveredIds(context: Context): Set<Int> =
        prefs(context).getStringSet(KEY_DISCOVERED, emptySet())!!
            .mapNotNull { it.toIntOrNull() }.toSet()

    fun addDiscoveredId(context: Context, id: Int) {
        val current = prefs(context).getStringSet(KEY_DISCOVERED, emptySet())!!.toMutableSet()
        current.add(id.toString())
        prefs(context).edit().putStringSet(KEY_DISCOVERED, current).apply()
    }
}
