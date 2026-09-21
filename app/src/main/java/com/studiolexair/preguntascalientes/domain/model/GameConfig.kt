package com.studiolexair.preguntascalientes.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Configuración de la partida (modo, categorías, intensidad, temporizador). */
@Parcelize
data class GameConfig(
    val mode: GameMode = GameMode.CALIENTES,
    val categories: Set<Category> = setOf(
        Category.CALIENTES, Category.INTERESANTES, Category.DIVERTIDAS,
        Category.ATREVIDAS, Category.ROMANTICAS, Category.CONFESIONES
    ),
    val intensity: Int = 2,        // 1=Suave, 2=Medio, 3=Extremo
    val timerSeconds: Int = 0,     // 0 = sin límite
    val maxTurns: Int = 0          // 0 = infinito (hasta que salgan)
) : Parcelable
