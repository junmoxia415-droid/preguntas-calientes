package com.studiolexair.preguntascalientes.data.questions

import android.content.Context
import com.studiolexair.preguntascalientes.data.db.DatabaseProvider
import com.studiolexair.preguntascalientes.domain.model.Category
import com.studiolexair.preguntascalientes.domain.model.Challenge
import com.studiolexair.preguntascalientes.domain.model.Player
import com.studiolexair.preguntascalientes.domain.model.Question
import com.studiolexair.preguntascalientes.utils.PrefsManager

/**
 * QuestionRepository — Selección inteligente de contenido (catálogo §25).
 *
 * La siguiente pregunta se decide por: categoría + intensidad + estado de relación
 * + historial de la partida + contenido personalizado del usuario (Room).
 * Garantiza que nunca se repita una pregunta dentro de la misma partida.
 *
 * Pack metadata para la sección COLECCIÓN (catálogo §18-20).
 */
object QuestionRepository {

    data class Pack(
        val category: Category,
        val builtinCount: Int
    )

    fun getPacks(): List<Pack> = Category.values().map { cat ->
        Pack(cat, PacksData.allQuestions.count { it.category == cat })
    }

    /** Total incluyendo personalizadas. */
    suspend fun totalWithCustom(context: Context): Int =
        PacksData.allQuestions.size + DatabaseProvider.get(context).customQuestionDao().count()

    fun builtinQuestions(): List<Question> = PacksData.allQuestions

    fun builtinChallenges(): List<Challenge> = PacksData.challenges

    suspend fun customQuestions(context: Context): List<Question> =
        DatabaseProvider.get(context).customQuestionDao().getAll().map { it.toQuestion() }

    /**
     * Pool inteligente de preguntas para un jugador concreto.
     * @param usedIds IDs ya usadas EN ESTA PARTIDA (nunca se repiten).
     */
    fun pickQuestionPool(
        categories: Set<Category>,
        intensity: Int,
        player: Player,
        usedIds: Set<Int>,
        familiarMode: Boolean,
        custom: List<Question> = emptyList(),
        forceExtreme: Boolean = false
    ): List<Question> {
        val source = PacksData.allQuestions + custom
        val maxIntensity = if (forceExtreme) 3 else intensity
        val minIntensity = if (forceExtreme) 3 else 1
        return source.filter { q ->
            val catOk = categories.contains(q.category)
            val intensityOk = q.intensity in minIntensity..maxIntensity
            val partnerOk = q.isForPlayer(player)
            val familiarOk = !familiarMode || (!q.category.adultOnly && q.intensity < 3)
            val notUsed = q.id !in usedIds
            catOk && intensityOk && partnerOk && familiarOk && notUsed
        }
    }

    fun pickChallengePool(
        intensity: Int,
        usedIds: Set<Int>,
        familiarMode: Boolean,
        kissAllowed: Boolean = true
    ): List<Challenge> = PacksData.challenges.filter { c ->
        val intensityOk = if (familiarMode) c.intensity < 3 else c.intensity <= intensity
        val kissOk = kissAllowed || !c.kiss
        val familiarOk = !familiarMode || !c.adultOnly
        c.id !in usedIds && intensityOk && kissOk && familiarOk
    }

    // ── Progreso de colección (preguntas vistas alguna vez) ──
    fun packProgress(context: Context, category: Category): Pair<Int, Int> {
        val discovered = PrefsManager.getDiscoveredIds(context)
        val total = PacksData.allQuestions.count { it.category == category }
        val seen = PacksData.allQuestions.count { it.category == category && it.id in discovered }
        return seen to total
    }

    fun totalDiscovered(context: Context): Pair<Int, Int> {
        val discovered = PrefsManager.getDiscoveredIds(context)
        return discovered.size to PacksData.allQuestions.size
    }

    fun markDiscovered(context: Context, questionId: Int) =
        PrefsManager.addDiscoveredId(context, questionId)
}
