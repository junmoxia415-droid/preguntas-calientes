package com.studiolexair.preguntascalientes.data.db

import android.content.Context
import com.studiolexair.preguntascalientes.domain.model.LevelSystem
import com.studiolexair.preguntascalientes.domain.model.Player

/**
 * StatsRepository — contadores globales y por jugador + desbloqueo de logros.
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
class StatsRepository(private val context: Context) {

    private val db get() = DatabaseProvider.get(context)

    companion object {
        const val KEY_GAMES = "games"
        const val KEY_QUESTIONS = "questions_answered"
        const val KEY_DARES = "dares_completed"
        const val KEY_EXTREMES = "extremes_completed"
        const val KEY_TOTAL_XP = "total_xp"
        const val KEY_MAX_STREAK = "max_streak_global"
    }

    suspend fun increment(key: String, by: Long = 1) {
        val current = db.statsDao().get(key) ?: 0
        db.statsDao().put(StatCounterEntity(key, current + by))
    }

    suspend fun get(key: String): Long = db.statsDao().get(key) ?: 0

    suspend fun setMax(key: String, value: Long) {
        if (value > (db.statsDao().get(key) ?: 0)) {
            db.statsDao().put(StatCounterEntity(key, value))
        }
    }

    suspend fun allCounters(): Map<String, Long> =
        db.statsDao().all().associate { it.key to it.value }

    suspend fun allPlayerStats(): List<PlayerStatEntity> = db.statsDao().allPlayers()

    /** Actualiza la ficha persistente del jugador tras la partida. */
    suspend fun recordPlayer(player: Player) {
        val prev = db.statsDao().getPlayer(player.name)
        db.statsDao().putPlayer(
            PlayerStatEntity(
                name = player.name,
                games = (prev?.games ?: 0) + 1,
                answered = (prev?.answered ?: 0) + player.answered,
                dares = (prev?.dares ?: 0) + player.daresDone,
                maxStreak = maxOf(prev?.maxStreak ?: 0, player.maxStreak),
                xp = (prev?.xp ?: 0) + player.xp
            )
        )
    }

    // ── Logros ──
    suspend fun unlockedKeys(): List<String> = db.achievementDao().unlockedKeys()

    /** Devuelve true si ES NUEVO (recién desbloqueado). */
    suspend fun tryUnlock(achievement: com.studiolexair.preguntascalientes.domain.model.Achievement): Boolean {
        val result = db.achievementDao().unlock(AchievementEntity(achievement.key))
        return result != -1L
    }

    /**
     * Revisa los logros al final de la partida y devuelve los NUEVOS desbloqueados.
     */
    suspend fun checkEndgameAchievements(players: List<Player>, discoveredCount: Int):
            List<com.studiolexair.preguntascalientes.domain.model.Achievement> {
        val newOnes = mutableListOf<com.studiolexair.preguntascalientes.domain.model.Achievement>()
        val A = com.studiolexair.preguntascalientes.domain.model.Achievement

        if (get(KEY_QUESTIONS) >= 1 && tryUnlock(A.FIRST_FIRE)) newOnes += A.FIRST_FIRE
        if (get(KEY_QUESTIONS) >= 10 && tryUnlock(A.STARTER)) newOnes += A.STARTER
        if (get(KEY_QUESTIONS) >= 100 && tryUnlock(A.CENTENARIO)) newOnes += A.CENTENARIO
        if (get(KEY_EXTREMES) >= 10 && tryUnlock(A.SIN_MIEDO)) newOnes += A.SIN_MIEDO
        if (get(KEY_DARES) >= 1 && tryUnlock(A.FIRST_DARE)) newOnes += A.FIRST_DARE
        if (get(KEY_DARES) >= 10 && tryUnlock(A.DARE_10)) newOnes += A.DARE_10
        if (players.size >= 8 && tryUnlock(A.PARTY_MASTER)) newOnes += A.PARTY_MASTER
        players.forEach { p ->
            if (p.maxStreak >= 10 && tryUnlock(A.IMPARABLE)) newOnes += A.IMPARABLE
            if (p.maxStreak >= 5 && tryUnlock(A.RACHA_5)) newOnes += A.RACHA_5
            if (p.level >= 5 && tryUnlock(A.LEVEL_5)) newOnes += A.LEVEL_5
        }
        if (get(KEY_GAMES) >= 10 && tryUnlock(A.GAMES_10)) newOnes += A.GAMES_10
        if (discoveredCount >= 50 && tryUnlock(A.COLLECTOR)) newOnes += A.COLLECTOR
        return newOnes
    }

    fun levelOf(xp: Int): Int = LevelSystem.levelForXp(xp)
}
