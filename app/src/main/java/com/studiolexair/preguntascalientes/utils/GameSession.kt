package com.studiolexair.preguntascalientes.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.studiolexair.preguntascalientes.domain.game.GameEngine
import com.studiolexair.preguntascalientes.domain.model.Category
import com.studiolexair.preguntascalientes.domain.model.GameConfig
import com.studiolexair.preguntascalientes.domain.model.GameMode
import com.studiolexair.preguntascalientes.domain.model.Player

/**
 * GameSession — Estado compartido entre actividades (V2.1).
 *
 * V2.1: la PLANTILLA de jugadores es persistente (SharedPreferences + Gson):
 * se gestiona desde el menú principal y aparece pre-cargada al entrar
 * a cualquier modo. La config (modo, categorías, intensidad) sigue siendo
 * de sesión.
 */
object GameSession {

    private const val PREFS = "preguntas_calientes_prefs"
    private const val KEY_ROSTER = "roster_json"
    private val gson = Gson()
    private var rosterLoaded = false

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

    // ── Plantilla persistente (§7, jugadores desde el menú) ──

    fun ensureRosterLoaded(context: Context) {
        if (rosterLoaded) return
        rosterLoaded = true
        val json = context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_ROSTER, null) ?: return
        try {
            val type = object : TypeToken<List<Player>>() {}.type
            val saved: List<Player> = gson.fromJson(json, type)
            players = saved.toMutableList()
        } catch (e: Exception) { /* plantilla corrupta → empezar vacía */ }
    }

    fun saveRoster(context: Context) {
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_ROSTER, gson.toJson(players)).apply()
    }

    fun addPlayer(
        name: String,
        hasPartner: Boolean,
        avatar: String,
        colorIndex: Int,
        pronoun: String?,
        partnerName: String?
    ): Player {
        val player = Player(
            id = (players.maxOfOrNull { it.id } ?: 0) + 1,
            name = name.trim(),
            hasPartner = hasPartner,
            avatar = avatar,
            colorIndex = colorIndex,
            pronoun = pronoun?.takeIf { it.isNotBlank() },
            partnerName = partnerName?.trim()?.takeIf { it.isNotEmpty() }
        )
        players.add(player)
        syncPartnerFor(player)
        return player
    }

    /**
     * Sincronización bidireccional de parejas:
     * - Si su pareja ya está en la lista → se vinculan ambos.
     * - Si alguien había escrito este nombre antes → se vincula al registrarse.
     */
    fun syncPartnerFor(player: Player) {
        val pName = player.partnerName
        if (pName != null) {
            players.firstOrNull { it.id != player.id && it.name.equals(pName, true) }?.let { partner ->
                partner.partnerName = player.name
                partner.hasPartner = true
                player.hasPartner = true
            }
        }
        players.firstOrNull {
            it.id != player.id && it.partnerName != null && it.partnerName.equals(player.name, true)
        }?.let { other ->
            if (player.partnerName == null) player.partnerName = other.name
            player.hasPartner = true
            other.hasPartner = true
        }
    }

    fun removePlayer(playerId: Int) {
        val removed = players.firstOrNull { it.id == playerId }
        players.removeAll { it.id == playerId }
        // Si era la pareja de alguien, desvincular (queda como nombre libre)
        if (removed != null) {
            players.firstOrNull { it.partnerName.equals(removed.name, true) }?.let { orphan ->
                orphan.partnerName = removed.name // conserva el nombre por si vuelve
                orphan.hasPartner = false
            }
        }
    }

    fun canStart(): Boolean {
        return when (mode) {
            GameMode.DUELO, GameMode.PAREJA -> players.size == 2
            else -> players.size >= mode.minPlayers
        }
    }

    fun startRequirement(): String = when (mode) {
        GameMode.DUELO -> "El modo ⚔️ Duelo necesita exactamente 2 jugadores"
        GameMode.PAREJA -> "El modo 💑 Pareja necesita exactamente 2 jugadores (tú y tu pareja)"
        else -> "Necesitas mínimo ${mode.minPlayers} jugadores"
    }

    /** Reset de configuración de partida (conserva la plantilla de jugadores). */
    fun resetMatch() {
        engine = null
        continueAvailable = false
        intensity = 2
        selectedCategories = mutableSetOf(
            Category.CALIENTES, Category.INTERESANTES, Category.DIVERTIDAS,
            Category.ATREVIDAS, Category.ROMANTICAS, Category.CONFESIONES
        )
        players.forEach { p ->
            p.xp = 0; p.points = 0; p.streak = 0; p.maxStreak = 0
            p.passesLeft = Player.MAX_PASSES; p.wildcardsLeft = Player.MAX_WILDCARDS
            p.answered = 0; p.daresDone = 0; p.kingRounds = 0
        }
    }
}
