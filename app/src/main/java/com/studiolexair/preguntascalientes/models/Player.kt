package com.studiolexair.preguntascalientes.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Modelo de jugador
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
@Parcelize
data class Player(
    val id: Int,
    val name: String,
    val hasPartner: Boolean
) : Parcelable {
    fun getStatusEmoji(): String = if (hasPartner) "👫" else "💔"
    fun getStatusText(): String = if (hasPartner) "Con Pareja" else "Soltero(a)"
}
