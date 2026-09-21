package com.studiolexair.preguntascalientes.models

/**
 * Modelo de pregunta
 * forPartnered:
 *  true  = solo para jugadores con pareja
 *  false = solo para solteros
 *  null  = para todos
 *
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
data class Question(
    val id: Int,
    val text: String,
    val category: Category,
    val forPartnered: Boolean? = null,
    val intensity: Int = 1 // 1=Suave, 2=Medio, 3=Extremo
) {
    fun isForPlayer(player: Player): Boolean {
        return when (forPartnered) {
            null -> true
            true -> player.hasPartner
            false -> !player.hasPartner
        }
    }
}
