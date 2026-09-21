package com.studiolexair.preguntascalientes.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Modelo de pregunta.
 * forPartnered: true = solo con pareja, false = solo solteros, null = universal.
 * isCustom: preguntas creadas por el usuario (Room).
 */
@Parcelize
data class Question(
    val id: Int,
    val text: String,
    val category: Category,
    val forPartnered: Boolean? = null,
    val intensity: Int = 1, // 1=Suave, 2=Medio, 3=Extremo
    val isCustom: Boolean = false
) : Parcelable {
    fun isForPlayer(player: Player): Boolean = when (forPartnered) {
        null -> true
        true -> player.hasPartner
        false -> !player.hasPartner
    }
}
