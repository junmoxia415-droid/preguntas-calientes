package com.studiolexair.preguntascalientes.domain.model

/**
 * Sistema de niveles: nivel n requiere n²·40 XP acumulado.
 * Nivel 2 → 160 XP, nivel 3 → 360, nivel 4 → 640, nivel 5 → 1000...
 */
object LevelSystem {
    fun levelForXp(xp: Int): Int {
        var level = 1
        while (xpForLevel(level + 1) <= xp && level < 99) level++
        return level
    }

    fun xpForLevel(level: Int): Int = level * level * 40

    fun xpNeededForNext(xp: Int): Int = xpForLevel(levelForXp(xp) + 1)

    /** Progreso 0..1 hacia el siguiente nivel. */
    fun progressFor(xp: Int): Float {
        val lvl = levelForXp(xp)
        val cur = xpForLevel(lvl)
        val next = xpForLevel(lvl + 1)
        return ((xp - cur).toFloat() / (next - cur)).coerceIn(0f, 1f)
    }
}
