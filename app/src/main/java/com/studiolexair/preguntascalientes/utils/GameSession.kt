package com.studiolexair.preguntascalientes.utils

import com.studiolexair.preguntascalientes.domain.game.GameEngine
import com.studiolexair.preguntascalientes.domain.model.Category
import com.studiolexair.preguntascalientes.domain.model.GameConfig
import com.studiolexair.preguntascalientes.domain.model.GameMode
import com.studiolexair.preguntascalientes.domain.model.Player

/**
 * GameSession — Estado compartido entre actividades (V2.0).
 * Mantiene jugadores, configuración y el motor de la partida en curso.
 */
object GameSession {

    var players: MutableList<Player> = mutableListOf()
    var selectedCategories: MutableSet<Category> = mutableSetOf(
        Category.CALIENTES, Category.INTERESANTES, Category.DIVERTIDAS,
        Category.ATREVIDAS, Category.ROMANTICAS, Category.CONFESIONES,
        Category.PAREJAS, Category.FIESTA
    )
    var intensity: Int = 2
    var mode: GameMode = GameMode.CALIENTES
    var timerSeconds: Int = 0
    var maxTurns: Int = 0

    var engine: GameEngine? = null
    var continueAvailable: Boolean = false

    fun config(): GameConfig = GameConfig(
        mode = mode,
        categories = selectedCategories.toSet(),
        intensity = intensity,
        timerSeconds = if (timerSeconds > 0) timerSeconds else 0,
        maxTurns = maxTurns
    )

    fun addPlayer(name: String, hasPartner: Boolean, avatar: String, colorIndex: Int, pronoun: String?): Player {
        val player = Player(
            id = (players.maxOfOrNull { it.id } ?: 0) + 1,
            name = name.trim(),
            hasPartner = hasPartner,
            avatar = avatar,
            colorIndex = colorIndex,
            pronoun = pronoun?.takeIf { it.isNotBlank() }
        )
        players.add(player)
        return player
    }

    fun removePlayer(playerId: Int) {
        players.removeAll { it.id == playerId }
    }

    fun canStart(): Boolean {
        val min = if (mode == GameMode.DUELO) 2 else mode.minPlayers
        val exact = when (mode) {
            GameMode.DUELO -> players.size == 2
            GameMode.PAREJA -> players.size == 2
            else -> players.size >= min
        }
        return exact
    }

    fun startRequirement(): String = when (mode) {
        GameMode.DUELO -> "El modo ⚔️ Duelo necesita exactamente 2 jugadores"
        GameMode.PAREJA -> "El modo 💑 Pareja necesita exactamente 2 jugadores (tú y tu pareja)"
        else -> "Necesitas mínimo ${mode.minPlayers} jugadores"
    }

    fun reset() {
        players = mutableListOf()
        engine = null
        continueAvailable = false
        intensity = 2
        mode = GameMode.CALIENTES
        selectedCategories = mutableSetOf(
            Category.CALIENTES, Category.INTERESANTES, Category.DIVERTIDAS,
            Category.ATREVIDAS, Category.ROMANTICAS, Category.CONFESIONES
        )
    }
}
