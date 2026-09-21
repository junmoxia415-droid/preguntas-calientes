package com.studiolexair.preguntascalientes.utils

import androidx.annotation.DrawableRes
import com.studiolexair.preguntascalientes.R
import com.studiolexair.preguntascalientes.domain.model.CardType
import com.studiolexair.preguntascalientes.domain.model.GameMode

/**
 * GameIcons — Iconos SVG propios del juego (Studio Lexair).
 * Mapea modos y tipos de carta a los vector drawables propios.
 */
object GameIcons {

    @DrawableRes
    fun forMode(mode: GameMode): Int = when (mode) {
        GameMode.CALIENTES -> R.drawable.ic_flame
        GameMode.VERDAD_RETO -> R.drawable.ic_mask
        GameMode.PAREJA -> R.drawable.ic_heart
        GameMode.PAREJAS -> R.drawable.ic_kiss
        GameMode.DIVERSION -> R.drawable.ic_dice
        GameMode.ATREVIDO -> R.drawable.ic_skull
        GameMode.CONFESIONES -> R.drawable.ic_target2
        GameMode.CARTAS -> R.drawable.ic_cards2
        GameMode.DUELO -> R.drawable.ic_duel
        GameMode.TODOS -> R.drawable.ic_bolt
        GameMode.ALEATORIO -> R.drawable.ic_dice
    }

    @DrawableRes
    fun forCardType(type: CardType): Int = when (type) {
        CardType.CALIENTE -> R.drawable.ic_flame
        CardType.EXTREMA -> R.drawable.ic_skull
        CardType.VERDAD -> R.drawable.ic_target2
        CardType.RETO -> R.drawable.ic_bolt
        CardType.BESO -> R.drawable.ic_kiss
        CardType.CONFESION -> R.drawable.ic_mask
        CardType.DOBLE_TURNO -> R.drawable.ic_dice
        CardType.CAMBIAR_JUGADOR -> R.drawable.ic_cards2
        CardType.TODOS_RESPONDEN -> R.drawable.ic_bolt
        CardType.ELIGE -> R.drawable.ic_target2
        CardType.COMODIN -> R.drawable.ic_cards2
        CardType.RIESGO -> R.drawable.ic_skull
        CardType.CORONA -> R.drawable.ic_crown2
        CardType.DUELO -> R.drawable.ic_duel
    }

    fun trophy(): Int = R.drawable.ic_trophy2
}
