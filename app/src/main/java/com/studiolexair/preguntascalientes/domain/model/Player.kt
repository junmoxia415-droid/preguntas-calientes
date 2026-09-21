package com.studiolexair.preguntascalientes.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Perfil de jugador V2.1: avatar (60 disponibles), color, pronombre,
 * estado de relación + PAREJA VINCULADA (partnerName, sincronización
 * bidireccional), XP, nivel, racha, pasos, comodines y estadísticas.
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
@Parcelize
data class Player(
    val id: Int,
    val name: String,
    var hasPartner: Boolean,
    val avatar: String = defaultAvatar(id),
    val colorIndex: Int = (id - 1) % PLAYER_COLORS.size,
    val pronoun: String? = null,
    /** Nombre de la pareja vinculada (jugador existente o escrito a mano). */
    var partnerName: String? = null,
    // Progresión en partida
    var xp: Int = 0,
    var points: Int = 0,
    var streak: Int = 0,
    var maxStreak: Int = 0,
    var passesLeft: Int = MAX_PASSES,
    var wildcardsLeft: Int = MAX_WILDCARDS,
    var answered: Int = 0,
    var daresDone: Int = 0,
    var kingRounds: Int = 0
) : Parcelable {

    /** Nivel derivado del XP: nivel n requiere n²·40 XP acumulados. */
    val level: Int get() = LevelSystem.levelForXp(xp)

    val streakMultiplier: Int get() = when {
        streak >= STREAK_LEGEND -> 5
        streak >= STREAK_FIRE -> 3
        streak >= STREAK_HOT -> 2
        else -> 1
    }

    fun getStatusEmoji(): String = if (hasPartner) "👫" else "💔"
    fun getStatusText(): String = if (hasPartner) "Con Pareja" else "Soltero(a)"

    /** Texto de vínculo para mostrar en la tarjeta del jugador. */
    fun partnerLabel(): String? = partnerName?.let { "❤️ con $it" }

    fun avatarColor(): Long = colorFor(colorIndex)

    fun addXpAndPoints(base: Int) {
        val total = base * streakMultiplier
        xp += total
        points += total
    }

    fun registerSuccess() {
        streak += 1
        if (streak > maxStreak) maxStreak = streak
        answered += 1
    }

    fun registerFail() {
        streak = 0
    }

    companion object {
        const val MAX_PASSES = 3
        const val MAX_WILDCARDS = 3
        const val STREAK_HOT = 3
        const val STREAK_FIRE = 5
        const val STREAK_LEGEND = 10

        /** 🎭 60 avatares disponibles para elegir. */
        val AVATAR_CHOICES = listOf(
            // Caritas y personajes
            "😎", "😈", "🥰", "🤪", "😜", "🤩", "🥵", "🥶", "🤯", "😏",
            "🙃", "😋", "🤓", "🥸", "🤑", "🤭", "🤫", "😴", "🤤", "😇",
            // Animales
            "🦊", "🐺", "🦁", "🐯", "🐻", "🐼", "🐨", "🐸", "🐵", "🐱",
            "🐶", "🐰", "🦄", "🐷", "🐙", "🦋", "🐢", "🦖", "🦩", "🐳",
            "🦈", "🐝", "🐞", "🐥", "🐧", "🐭",
            // Fantasía
            "👽", "🤖", "👻", "💀", "🎃", "👸", "🤴", "🧙", "🧛", "🧜",
            "🦸", "🧚", "🍀", "🔥"
        )

        val PLAYER_COLORS = listOf(
            0xFFFF6B9D, 0xFF9D7BFF, 0xFF4ADE80, 0xFFFFD93D,
            0xFF4FC3F7, 0xFFFF7A3D, 0xFFF06292, 0xFF4DB6AC
        )

        fun defaultAvatar(id: Int): String =
            AVATAR_CHOICES[(id - 1).coerceAtLeast(0) % AVATAR_CHOICES.size]

        fun colorFor(index: Int): Long = PLAYER_COLORS[index % PLAYER_COLORS.size]
    }
}
