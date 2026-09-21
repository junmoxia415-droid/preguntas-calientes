package com.studiolexair.preguntascalientes.domain.model

/**
 * Carta jugable: envuelve una pregunta, un reto o un evento especial.
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
data class GameCard(
    val type: CardType,
    val question: Question? = null,
    val challenge: Challenge? = null,
    val specialTitle: String? = null,
    val specialText: String? = null
) {
    val isSpecial: Boolean get() = specialTitle != null

    val frontEmoji: String get() = when {
        isSpecial -> type.emoji
        challenge != null -> type.emoji
        else -> question?.category?.emoji ?: "🃏"
    }

    fun contentText(): String = when {
        isSpecial -> specialText ?: ""
        challenge != null -> challenge.text
        question != null -> question.text
        else -> ""
    }

    fun headerText(): String {
        if (isSpecial) return specialTitle ?: type.title()
        val base = when {
            challenge != null -> type.title()
            question != null -> "${question.category.emoji} ${question.category.displayName}"
            else -> type.title()
        }
        return if (type.isQuestion && type != CardType.CALIENTE && type != CardType.VERDAD) {
            "$base · ${type.emoji} ${type.displayName}"
        } else base
    }

    fun intensityOf(): Int = question?.intensity ?: challenge?.intensity ?: 1

    fun baseXp(): Int = when {
        challenge != null -> challenge.xpReward
        question != null -> 10 + question.intensity * 10 // 20/30/40
        else -> type.bonusXp
    }
}
