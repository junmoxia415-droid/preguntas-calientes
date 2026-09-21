package com.studiolexair.preguntascalientes.data.db

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.studiolexair.preguntascalientes.domain.game.GameEngine
import com.studiolexair.preguntascalientes.domain.model.Category
import com.studiolexair.preguntascalientes.domain.model.GameConfig
import com.studiolexair.preguntascalientes.domain.model.GameMode
import com.studiolexair.preguntascalientes.domain.model.Player

/**
 * SessionRepository — Guardar / continuar partidas (catálogo §26).
 * Serializa el estado del GameEngine con Gson en Room (offline-first).
 */
object SessionRepository {

    private val gson = Gson()

    private data class Snapshot(
        val players: List<Player>,
        val config: GameConfig,
        val turnIndex: Int,
        val totalTurns: Int,
        val usedQuestions: Set<Int>,
        val usedChallenges: Set<Int>,
        val doubleNext: Boolean,
        val familiar: Boolean
    )

    suspend fun save(context: Context, engine: GameEngine, familiar: Boolean) {
        val snap = Snapshot(
            players = engine.players,
            config = engine.config,
            turnIndex = engine.turnIndex,
            totalTurns = engine.totalTurns,
            usedQuestions = engine.usedQuestionIds.toSet(),
            usedChallenges = engine.usedChallengeIds.toSet(),
            doubleNext = engine.doublePointsNext,
            familiar = familiar
        )
        DatabaseProvider.get(context).savedGameDao()
            .save(SavedGameEntity(json = gson.toJson(snap)))
    }

    /** Restaura el motor desde la partida guardada, o null si no hay. */
    suspend fun restore(context: Context): GameEngine? {
        val entity = DatabaseProvider.get(context).savedGameDao().load() ?: return null
        return try {
            val type = object : TypeToken<Snapshot>() {}.type
            val snap: Snapshot = gson.fromJson(entity.json, type)
            val engine = GameEngine(snap.players.toMutableList(), snap.config, snap.familiar)
            engine.restoreState(snap.turnIndex, snap.totalTurns, snap.usedQuestions, snap.usedChallenges, snap.doubleNext)
            engine
        } catch (e: Exception) {
            clear(context); null
        }
    }

    suspend fun clear(context: Context) = DatabaseProvider.get(context).savedGameDao().clear()

    /** Información breve para el botón "Continuar" del menú. */
    suspend fun summary(context: Context): String? {
        val entity = DatabaseProvider.get(context).savedGameDao().load() ?: return null
        return try {
            val type = object : TypeToken<Snapshot>() {}.type
            val snap: Snapshot = gson.fromJson(entity.json, type)
            "${snap.config.mode.emoji} ${snap.config.mode.displayName} · ${snap.players.size} jugadores · turno ${snap.totalTurns}"
        } catch (e: Exception) { null }
    }
}

