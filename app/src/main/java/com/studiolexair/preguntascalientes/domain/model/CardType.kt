package com.studiolexair.preguntascalientes.domain.model

/**
 * Tipos de carta del mazo (catálogo §2). Cada carta tiene un efecto.
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
enum class CardType(
    val emoji: String,
    val displayName: String,
    val isQuestion: Boolean = false,
    val isChallenge: Boolean = false,
    val isEvent: Boolean = false,
    val bonusXp: Int = 0
) {
    CALIENTE("🔥", "Carta Caliente", isQuestion = true),
    EXTREMA("😈", "Carta Extrema", isQuestion = true, bonusXp = 10),
    VERDAD("🎤", "Verdad", isQuestion = true),
    RETO("💪", "Reto", isChallenge = true),
    BESO("💋", "Beso", isChallenge = true),
    CONFESION("🎭", "Confesión", isQuestion = true),
    DOBLE_TURNO("🎲", "Doble Turno", isQuestion = true),
    CAMBIAR_JUGADOR("🔄", "Cambiar Jugador", isQuestion = true, isEvent = true),
    TODOS_RESPONDEN("⚡", "Todos Responden", isQuestion = true, isEvent = true),
    ELIGE("🎯", "Elige a Alguien", isQuestion = true, isEvent = true),
    COMODIN("🃏", "Comodín", isEvent = true, bonusXp = 20),
    RIESGO("💀", "Riesgo", isQuestion = true, isEvent = true, bonusXp = 30),
    CORONA("👑", "Rey/Reina de la Ronda", isEvent = true, bonusXp = 30),
    DUELO("⚔️", "Duelo", isQuestion = true, isEvent = true);

    fun title(): String = "$emoji $displayName"

    companion object {
        /** Pool de cartas especiales (sin las básicas por modo). */
        val SPECIALS = listOf(
            DOBLE_TURNO, CAMBIAR_JUGADOR, TODOS_RESPONDEN, ELIGE,
            COMODIN, RIESGO, CORONA, BESO
        )
    }
}
