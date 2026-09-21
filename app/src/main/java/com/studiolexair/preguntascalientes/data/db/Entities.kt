package com.studiolexair.preguntascalientes.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.studiolexair.preguntascalientes.domain.model.Category
import com.studiolexair.preguntascalientes.domain.model.Question

/** Pregunta creada por el usuario (catálogo §19). IDs a partir de 100000. */
@Entity(tableName = "custom_questions")
data class CustomQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val category: String,
    val intensity: Int,
    val forPartnered: Int,          // -1=todos, 0=solteros, 1=con pareja
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toQuestion(): Question = Question(
        id = CUSTOM_ID_OFFSET + id,
        text = text,
        category = Category.fromString(category) ?: Category.DIVERTIDAS,
        forPartnered = when (forPartnered) { 1 -> true; 0 -> false; else -> null },
        intensity = intensity.coerceIn(1, 3),
        isCustom = true
    )

    companion object { const val CUSTOM_ID_OFFSET = 100_000 }
}

/** Contador genérico de estadísticas (clave-valor). */
@Entity(tableName = "stats")
data class StatCounterEntity(
    @PrimaryKey val key: String,
    val value: Long
)

/** Estadísticas acumuladas por jugador (por nombre). */
@Entity(tableName = "player_stats")
data class PlayerStatEntity(
    @PrimaryKey val name: String,
    val games: Int = 0,
    val answered: Int = 0,
    val dares: Int = 0,
    val maxStreak: Int = 0,
    val xp: Int = 0
)

/** Logro desbloqueado. */
@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val key: String,
    val unlockedAt: Long = System.currentTimeMillis()
)

/** Partida guardada para "Continuar partida" (catálogo §26). */
@Entity(tableName = "saved_games")
data class SavedGameEntity(
    @PrimaryKey val id: Int = 1,
    val json: String,
    val updatedAt: Long = System.currentTimeMillis()
)
