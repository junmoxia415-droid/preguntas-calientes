package com.studiolexair.preguntascalientes.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Reto físico/grupal (para Verdad o Reto, cartas RETO/BESO, gana +50 XP...).
 */
@Parcelize
data class Challenge(
    val id: Int,
    val text: String,
    val intensity: Int = 1,   // 1=Suave, 2=Medio, 3=Extremo
    val kiss: Boolean = false,
    val adultOnly: Boolean = false
) : Parcelable {
    val xpReward: Int get() = 20 + intensity * 15 // 35 / 50 / 65
}
