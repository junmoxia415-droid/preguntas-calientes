package com.studiolexair.preguntascalientes.domain.model

/**
 * Categorías de preguntas disponibles (V2.0)
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
enum class Category(
    val displayName: String,
    val emoji: String,
    val adultOnly: Boolean = false
) {
    CALIENTES("Calientes", "🔥", adultOnly = true),
    INTERESANTES("Interesantes", "🤔"),
    DIVERTIDAS("Divertidas", "😄"),
    ATREVIDAS("Atrevidas", "🌶️", adultOnly = true),
    ROMANTICAS("Románticas", "💑"),
    CONFESIONES("Confesiones", "🎭"),
    PAREJAS("Parejas", "💋"),
    FIESTA("Fiesta", "🎉");

    companion object {
        fun fromString(name: String): Category? =
            values().find { it.name.equals(name, ignoreCase = true) }

        /** Categorías visibles según el filtro de contenido (modo familiar). */
        fun visible(familiarMode: Boolean): List<Category> =
            if (familiarMode) values().filter { !it.adultOnly }
            else values().toList()
    }
}
