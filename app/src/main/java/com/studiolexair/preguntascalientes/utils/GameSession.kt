package com.studiolexair.preguntascalientes.utils

import com.studiolexair.preguntascalientes.models.Category
import com.studiolexair.preguntascalientes.models.Player

/**
 * Singleton para mantener datos del juego entre actividades
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
object GameSession {
    var players: MutableList<Player> = mutableListOf()
    var selectedCategories: MutableSet<Category> = mutableSetOf()
    var intensity: Int = 2 // 1=Suave, 2=Medio, 3=Extremo
    var currentPlayerIndex: Int = 0
    var askedQuestionIds: MutableSet<Int> = mutableSetOf()

    fun reset() {
        players.clear()
        selectedCategories.clear()
        intensity = 2
        currentPlayerIndex = 0
        askedQuestionIds.clear()
    }

    fun resetGameOnly() {
        currentPlayerIndex = 0
        askedQuestionIds.clear()
    }

    fun getCurrentPlayer(): Player? {
        if (players.isEmpty()) return null
        return players[currentPlayerIndex % players.size]
    }

    fun nextPlayer() {
        if (players.isNotEmpty()) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size
        }
    }

    fun addPlayer(name: String, hasPartner: Boolean): Player {
        val player = Player(
            id = players.size + 1,
            name = name.trim(),
            hasPartner = hasPartner
        )
        players.add(player)
        return player
    }

    fun removePlayer(playerId: Int) {
        players.removeAll { it.id == playerId }
        // Reasignar IDs
        players = players.mapIndexed { index, player ->
            player.copy(id = index + 1)
        }.toMutableList()
    }
}
