package com.studiolexair.preguntascalientes.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.studiolexair.preguntascalientes.R
import com.studiolexair.preguntascalientes.utils.PrefsManager

/**
 * SoundManager (catálogo §13 + §42): efectos UI/gameplay y música
 * dinámica según intensidad — suave/medio = chill, extremo = intensa.
 * Audio sintetizado, 100% offline.
 */
object SoundManager {

    private const val TAG = "SoundManager"

    // SFX disponibles
    const val SFX_CLICK = R.raw.sfx_click
    const val SFX_POP = R.raw.sfx_pop
    const val SFX_DING = R.raw.sfx_ding
    const val SFX_WHOOSH = R.raw.sfx_whoosh
    const val SFX_TICK = R.raw.sfx_tick
    const val SFX_FLIP = R.raw.sfx_flip
    const val SFX_EVENT = R.raw.sfx_event
    const val SFX_FANFARE = R.raw.sfx_fanfare
    const val SFX_COUNT = R.raw.sfx_count
    const val SFX_GO = R.raw.sfx_go

    private val sfxPlayers = mutableMapOf<Int, MediaPlayer>()
    private var musicPlayer: MediaPlayer? = null
    private var currentMusic = 0
    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        initialized = true
        listOf(SFX_CLICK, SFX_POP, SFX_DING, SFX_WHOOSH, SFX_TICK,
            SFX_FLIP, SFX_EVENT, SFX_FANFARE, SFX_COUNT, SFX_GO
        ).forEach { res ->
            try {
                MediaPlayer.create(context.applicationContext, res)?.let { sfxPlayers[res] = it }
            } catch (e: Exception) { Log.w(TAG, "No SFX $res", e) }
        }
        refreshVolumes(context)
    }

    private fun sfxLevel(context: Context): Float =
        PrefsManager.sfxVolume(context) / 100f

    private fun musicLevel(context: Context): Float =
        PrefsManager.musicVolume(context) / 100f * 0.7f

    fun refreshVolumes(context: Context) {
        val sfx = sfxLevel(context)
        sfxPlayers.values.forEach { p ->
            try { p.setVolume(sfx, sfx) } catch (_: Exception) {}
        }
        val m = musicLevel(context)
        try { musicPlayer?.setVolume(m, m) } catch (_: Exception) {}
    }

    fun play(context: Context, sfxRes: Int) {
        if (PrefsManager.sfxVolume(context) == 0) return
        try {
            sfxPlayers[sfxRes]?.let { p ->
                if (p.isPlaying) { p.pause(); p.seekTo(0) }
                val v = sfxLevel(context)
                p.setVolume(v, v)
                p.start()
            }
        } catch (e: Exception) { Log.w(TAG, "play failed", e) }
    }

    fun click(context: Context) = play(context, SFX_CLICK)

    // ── Música por intensidad (§42) ──
    /** intensity: 1-2 suave/medio => chill, 3 extremo => intensa. 0 => parar. */
    fun playMusic(context: Context, intensity: Int) {
        val wanted = when {
            intensity == 0 -> 0
            intensity >= 3 -> R.raw.music_intense
            else -> R.raw.music_chill
        }
        if (wanted == currentMusic && musicPlayer?.isPlaying == true) return
        stopMusic()
        if (wanted == 0 || PrefsManager.musicVolume(context) == 0) return
        try {
            musicPlayer = MediaPlayer.create(context.applicationContext, wanted)?.apply {
                isLooping = true
                val v = musicLevel(context)
                setVolume(v, v)
                start()
            }
            currentMusic = wanted
        } catch (e: Exception) { Log.w(TAG, "music failed", e) }
    }

    fun pauseMusic() { try { musicPlayer?.pause() } catch (_: Exception) {} }

    fun resumeMusic(context: Context) {
        if (PrefsManager.musicVolume(context) == 0) return
        try {
            musicPlayer?.let { p -> if (!p.isPlaying) p.start() }
        } catch (_: Exception) {}
    }

    fun stopMusic() {
        try { musicPlayer?.stop(); musicPlayer?.release() } catch (_: Exception) {}
        musicPlayer = null
        currentMusic = 0
    }
}
