package com.studiolexair.preguntascalientes.domain.model

/**
 * Logros desbloqueables (catálogo §28).
 */
enum class Achievement(
    val key: String,
    val emoji: String,
    val title: String,
    val description: String,
    val target: Int
) {
    FIRST_FIRE("first_fire", "🔥", "Primer fuego", "Responde tu primera pregunta", 1),
    STARTER("starter_10", "⭐", "Calentando", "Responde 10 preguntas", 10),
    CENTENARIO("centenario", "💯", "Centenario", "Responde 100 preguntas", 100),
    SIN_MIEDO("sin_miedo", "😈", "Sin miedo", "Completa 10 preguntas extremas", 10),
    PARTY_MASTER("party_master", "👥", "Party Master", "Juega con 8 o más personas", 8),
    IMPARABLE("imparable", "🔥", "Imparable", "Consigue una racha de x10", 10),
    RACHA_5("racha_5", "⚡", "En racha", "Consigue una racha de x5", 5),
    FIRST_DARE("first_dare", "💪", "Valiente", "Completa tu primer reto", 1),
    DARE_10("dare_10", "🎯", "Retador", "Completa 10 retos", 10),
    LEVEL_5("level_5", "🏅", "Leyenda local", "Alcanza el nivel 5 con un jugador", 5),
    GAMES_10("games_10", "🎮", "Viciado", "Juega 10 partidas", 10),
    COLLECTOR("collector", "📚", "Coleccionista", "Descubre 50 preguntas distintas", 50);

    fun label(): String = "$emoji $title"
}
