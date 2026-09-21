package com.studiolexair.preguntascalientes.domain.model

/**
 * Modos de juego V2.0 (catálogo §1).
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
enum class GameMode(
    val displayName: String,
    val emoji: String,
    val description: String,
    val minPlayers: Int = 2,
    val usesTruthDare: Boolean = false,
    val usesDuel: Boolean = false,
    val couplesOnly: Boolean = false,
    val specialCardsMultiplier: Float = 1f
) {
    CALIENTES("Preguntas Calientes", "🔥",
        "El clásico: preguntas picantes por turnos con intensidad al gusto."),
    VERDAD_RETO("Verdad o Reto", "🎭",
        "Elige VERDAD para responder o RETO para atreverte. +50 XP por reto.",
        usesTruthDare = true),
    PAREJA("Modo Pareja", "💑",
        "Diseñado para dos: romántico, picante, atrevido o confesiones.",
        couplesOnly = true),
    PAREJAS("Modo Parejas", "💋",
        "Grupo de parejas: preguntas y retos pensados para jugar de a dos."),
    DIVERSION("Solo Diversión", "😂",
        "300% seguro: solo preguntas divertidas y de fiesta."),
    ATREVIDO("Atrevido", "😈",
        "Sin límites: intensidad extrema desde la primera carta."),
    CONFESIONES("Confesiones", "🧠",
        "Secretos, verdades incómodas y confesiones profundas."),
    CARTAS("Cartas Especiales", "🃏",
        "Mazo con efectos: doble turno, todos responden, riesgo, comodines...",
        specialCardsMultiplier = 2.2f),
    DUELO("Duelo", "⚔️",
        "Cara a cara: el grupo vota quién responde mejor. Solo 2 jugadores.",
        usesDuel = true),
    TODOS("Todos contra todos", "👥",
        "Todos responden todo. Ideal para grupos grandes.",
        specialCardsMultiplier = 1.8f),
    ALEATORIO("Modo Aleatorio", "🎲",
        "La app decide: categorías, intensidad y caos total.",
        specialCardsMultiplier = 1.4f);

    fun title(): String = "$emoji $displayName"
}
