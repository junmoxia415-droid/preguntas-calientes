package com.studiolexair.preguntascalientes.models

/**
 * Categorías de preguntas disponibles
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
enum class Category(val displayName: String, val emoji: String) {
    CALIENTES("Calientes", "🔥"),
    INTERESANTES("Interesantes", "🤔"),
    DIVERTIDAS("Divertidas", "😄"),
    ATREVIDAS("Atrevidas", "🌶️"),
    ROMANTICAS("Románticas", "💑"),
    CONFESIONES("Confesiones", "🎭");

    companion object {
        fun fromString(name: String): Category? {
            return values().find { it.name.equals(name, ignoreCase = true) }
        }
    }
}
