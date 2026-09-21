package com.studiolexair.preguntascalientes.domain.game

import com.studiolexair.preguntascalientes.domain.model.CardType
import com.studiolexair.preguntascalientes.domain.model.Category
import com.studiolexair.preguntascalientes.domain.model.Challenge
import com.studiolexair.preguntascalientes.domain.model.GameCard
import com.studiolexair.preguntascalientes.domain.model.GameConfig
import com.studiolexair.preguntascalientes.domain.model.GameMode
import com.studiolexair.preguntascalientes.domain.model.Player
import com.studiolexair.preguntascalientes.domain.model.Question
import kotlin.random.Random

/**
 * GameEngine — Máquina de estados de la partida (V2.0).
 *
 * Implementa del catálogo: sistema de cartas (§2), puntuación/XP/racha (§3-4),
 * retos (§5), eventos aleatorios (§6), pasar (§16), comodines (§17),
 * momentos especiales cada 10/25/50 turnos (§43), duelos (§23),
 * verdad o reto, y selección inteligente anti-repetición (§25).
 *
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
class GameEngine(
    val players: MutableList<Player>,
    val config: GameConfig,
    private val familiarMode: Boolean = false
) {
    /** Resultado de repartir una carta. */
    class Deal(
        val card: GameCard?,
        val banner: String? = null,          // ⚠️ texto de evento para mostrar
        val truthOrDareChoice: Boolean = false,
        val doublePoints: Boolean = false,
        val needsPlayerPick: Boolean = false, // carta ELIGE: el jugador escoge objetivo
        val roundTitle: String? = null        // "🎉 RONDA ESPECIAL" etc
    )

    /** Opciones del menú de comodines (§17). */
    enum class Wildcard(val emoji: String, val label: String) {
        CHANGE_CARD("🔄", "Cambiar carta"),
        FREE_PASS("🛡️", "Pasar gratis"),
        CHANGE_PLAYER("👤", "Cambiar jugador"),
        DOUBLE_POINTS("✖️2", "Duplicar puntos de la carta"),
        CHOOSE_CATEGORY("🎯", "Elegir categoría")
    }

    var turnIndex = 0
        private set
    var totalTurns = 0
        private set

    private val questions: MutableList<Question> = mutableListOf()
    private val challenges: MutableList<Challenge> = mutableListOf()
    val usedQuestionIds = mutableSetOf<Int>()
    val usedChallengeIds = mutableSetOf<Int>()

    var doublePointsNext = false
        private set
    private var keepTurn = false
    private var pendingDuel = false

    var lastCard: GameCard? = null
        private set

    val currentPlayer: Player get() = players[turnIndex % players.size]

    fun loadContent(allQuestions: List<Question>, custom: List<Question>, allChallenges: List<Challenge>) {
        questions.clear(); questions.addAll(allQuestions + custom)
        challenges.clear(); challenges.addAll(if (familiarMode) allChallenges.filter { !it.adultOnly } else allChallenges)
    }

    // ═════════════════ REPARTO DE CARTAS ═════════════════

    /** Reparte la siguiente carta respetando modo, rondas especiales y eventos. */
    fun deal(): Deal {
        if (players.isEmpty()) return Deal(null)
        if (config.maxTurns > 0 && totalTurns >= config.maxTurns) return Deal(null, banner = "END")

        totalTurns++

        // ── §43 Momentos especiales: rondas 10 / 25 / 50 ──
        when (totalTurns % SPECIAL_EVERY) {
            0 -> {
                val milestone = totalTurns / SPECIAL_EVERY
                return when {
                    milestone % 5 == 0 -> { // turno 50: ronda final
                        doublePointsNext = true
                        specialCard(
                            CardType.TODOS_RESPONDEN,
                            "👑 RONDA FINAL",
                            "¡Última ronda legendaria! TODOS responden y TODO vale doble.",
                            roundTitle = "👑 RONDA FINAL"
                        )
                    }
                    milestone % 3 == 0 || milestone == 2 -> { // 20/25/30...: ronda caliente
                        forceExtremeOnce = true
                        specialCard(
                            CardType.EXTREMA,
                            "🔥 RONDA CALIENTE",
                            "Sube la temperatura: la siguiente carta es de intensidad EXTREMA.",
                            roundTitle = "🔥 RONDA CALIENTE"
                        )
                    }
                    else -> { // turnos 10, 10k: ronda especial
                        specialCard(
                            CardType.TODOS_RESPONDEN,
                            "🎉 RONDA ESPECIAL",
                            "¡Todos responden la siguiente carta! Preparaos.",
                            roundTitle = "🎉 RONDA ESPECIAL"
                        )
                    }
                }
            }
        }

        // ── Modo verdad o reto: primero elegir VERDAD o RETO ──
        if (config.mode.usesTruthDare && !pendingDuel) {
            return Deal(null, truthOrDareChoice = true)
        }

        // ── Modo duelo: cada 3 turnos es DUELO ──
        if (config.mode.usesDuel && totalTurns % 3 == 0) {
            return duelCard()
        }

        // ── §6 Eventos aleatorios / cartas especiales ──
        val specialChance = 0.13f * config.mode.specialCardsMultiplier + playerCountBonus()
        if (Random.nextFloat() < specialChance) {
            return randomSpecialCard()
        }

        return questionCard(cardTypeForMode())
    }

    /** Decide el tipo de carta de pregunta según el modo. */
    private fun cardTypeForMode(): CardType = when (config.mode) {
        GameMode.CALIENTES -> CardType.CALIENTE
        GameMode.CONFESIONES -> CardType.CONFESION
        GameMode.PAREJA, GameMode.PAREJAS -> if (Random.nextInt(4) == 0) CardType.BESO else CardType.CALIENTE
        GameMode.ATREVIDO -> if (Random.nextInt(3) == 0) CardType.EXTREMA else CardType.CALIENTE
        else -> CardType.CALIENTE
    }

    val pendingTruthOrDare: Boolean get() = config.mode.usesTruthDare && lastCard == null

    /** Jugador eligió VERDAD en modo Verdad o Reto. */
    fun dealTruth(): Deal = questionCard(CardType.VERDAD)

    /** Jugador eligió RETO. */
    fun dealDare(): Deal = dareCard()

    // ── Cartas concretas ──

    private var forceExtremeOnce = false

    private fun questionCard(type: CardType, forcedCategory: Category? = null): Deal {
        val player = currentPlayer
        val cats: Set<Category> = if (forcedCategory != null) setOf(forcedCategory) else effectiveCategories()
        val extreme = forceExtremeOnce || config.mode == GameMode.ATREVIDO
        forceExtremeOnce = false

        var pool = questions.filter { q ->
            q.category in cats &&
                q.intensity in (if (extreme) 3..3 else 1..config.intensity) &&
                q.isForPlayer(player) &&
                (!familiarMode || (!q.category.adultOnly && q.intensity < 3)) &&
                q.id !in usedQuestionIds
        }
        if (pool.isEmpty()) { // anti-repetición: reiniciar ciclo de esa categoría
            usedQuestionIds.clear()
            pool = questions.filter { q ->
                q.category in cats && q.intensity in (if (extreme) 3..3 else 1..config.intensity) &&
                    q.isForPlayer(player) && (!familiarMode || (!q.category.adultOnly && q.intensity < 3))
            }
        }
        val q = pool.randomOrNull()
        if (q == null) return Deal(null, banner = "Sin preguntas disponibles para estos filtros 😢")
        usedQuestionIds += q.id
        val finalType = when {
            extreme && type.isQuestion -> CardType.EXTREMA
            type == CardType.BESO -> { lastCard = kissCardOr(type, q); return questionAsCard() }
            config.mode.usesDuel && pendingDuel -> CardType.DUELO
            else -> type
        }
        pendingDuel = false
        lastCard = GameCard(finalType, question = q)
        return Deal(lastCard, doublePoints = doublePointsNext)
    }

    private fun questionAsCard(): Deal = Deal(lastCard, doublePoints = doublePointsNext)

    private fun kissCardOr(type: CardType, q: Question): GameCard {
        // Carta BESO: mitad pregunta romántica, mitad reto de beso
        val kissChallenges = challenges.filter { it.kiss && it.id !in usedChallengeIds }
        return if (kissChallenges.isNotEmpty() && Random.nextBoolean()) {
            val c = kissChallenges.random(); usedChallengeIds += c.id
            GameCard(CardType.BESO, challenge = c)
        } else GameCard(CardType.CALIENTE, question = q)
    }

    private fun dareCard(): Deal {
        val player = currentPlayer
        var pool = challenges.filter { c ->
            c.id !in usedChallengeIds &&
                c.intensity <= (if (config.mode == GameMode.ATREVIDO) 3 else config.intensity) &&
                (!familiarMode || (!c.adultOnly && c.intensity < 3))
        }
        if (pool.isEmpty()) { usedChallengeIds.clear(); pool = challenges }
        val c = pool.randomOrNull() ?: return Deal(null, banner = "Sin retos disponibles 😢")
        usedChallengeIds += c.id
        // En modo VERDAD_RETO el jugador NUNCA está obligado (§16): puede usar PASAR
        lastCard = GameCard(if (c.kiss) CardType.BESO else CardType.RETO, challenge = c)
        return Deal(lastCard, doublePoints = doublePointsNext)
    }

    private fun duelCard(): Deal {
        pendingDuel = true
        return questionCard(CardType.DUELO).let { deal ->
            Deal(
                deal.card,
                banner = "⚔️ DUELO: ambos jugadores responden, el grupo vota al mejor",
                doublePoints = deal.doublePoints
            )
        }
    }

    private fun specialCard(type: CardType, title: String, text: String, roundTitle: String? = null): Deal {
        lastCard = GameCard(type, specialTitle = title, specialText = text)
        return Deal(lastCard, banner = title, roundTitle = roundTitle)
    }

    private fun randomSpecialCard(): Deal {
        val type = CardType.SPECIALS.random()
        return when (type) {
            CardType.DOBLE_TURNO -> questionCard(CardType.DOBLE_TURNO)
            CardType.CAMBIAR_JUGADOR -> {
                val target = players.filter { it.id != currentPlayer.id }.random()
                specialCard(type, "🔄 CAMBIAR JUGADOR", "¡El turno salta a ${target.name}!")
                    .also { switchToPlayer(target.id) }
            }
            CardType.TODOS_RESPONDEN ->
                specialCard(type, "⚡ TODOS RESPONDEN", "¡Todos responden la siguiente carta! Cada acierto suma a cada uno.")
            CardType.ELIGE -> {
                val deal = questionCard(type)
                Deal(deal.card, banner = "🎯 ${currentPlayer.name} elige quién responde", needsPlayerPick = true)
            }
            CardType.COMODIN -> {
                currentPlayer.wildcardsLeft = (currentPlayer.wildcardsLeft + 1).coerceAtMost(5)
                currentPlayer.addXpAndPoints(20)
                specialCard(type, "🃏 ¡COMODÍN GRATIS!", "${currentPlayer.name} gana +1 comodín y +20 XP. ¡Suerte! 🎉")
            }
            CardType.RIESGO -> questionCard(CardType.RIESGO)
            CardType.CORONA -> {
                currentPlayer.kingRounds++
                currentPlayer.addXpAndPoints(30)
                specialCard(type, "👑 REY/REINA DE LA RONDA", "${currentPlayer.name} lleva la corona: +30 XP y decide el orden de los aplausos 👏")
            }
            CardType.BESO -> dareCard().let { Deal(it.card?.copy(type = CardType.BESO) ?: it.card) }
            else -> questionCard(CardType.CALIENTE)
        }
    }

    private fun playerCountBonus(): Float = when {
        players.size >= 9 -> 0.12f   // modo caos (§21)
        players.size >= 5 -> 0.07f   // modo fiesta
        else -> 0f
    }

    private fun effectiveCategories(): Set<Category> = when (config.mode) {
        GameMode.DIVERSION -> setOf(Category.DIVERTIDAS, Category.FIESTA)
        GameMode.CONFESIONES -> setOf(Category.CONFESIONES, Category.INTERESANTES)
        GameMode.PAREJA, GameMode.PAREJAS -> setOf(Category.PAREJAS, Category.ROMANTICAS, Category.CALIENTES, Category.ATREVIDAS)
        GameMode.CALIENTES -> setOf(Category.CALIENTES, Category.ATREVIDAS, Category.CONFESIONES)
        GameMode.ATREVIDO -> setOf(Category.ATREVIDAS, Category.CALIENTES, Category.CONFESIONES)
        else -> config.categories
    }

    // ═════════════════ RESOLUCIÓN (§3-4-5, §16-17) ═════════════════

    data class ResolveResult(
        val gainedXp: Int,
        val newStreak: Int,
        val multiplier: Int,
        val streakMessage: String? = null,   // 🔥 ¡RACHA x3!
        val levelUp: Boolean = false,
        val newLevel: Int = 1
    )

    /** El jugador respondió / completó el reto. */
    fun resolveSuccess(forPlayer: Player = currentPlayer): ResolveResult {
        val card = lastCard
        val base = when (card?.type) {
            CardType.RIESGO -> card.baseXp() * 2              // paga el riesgo
            else -> (card?.baseXp() ?: 15)
        }
        val wasLevel = forPlayer.level
        forPlayer.registerSuccess()
        if (card?.challenge != null) forPlayer.daresDone++
        val before = forPlayer.xp
        forPlayer.addXpAndPoints(if (doublePointsNext) base * 2 else base)
        doublePointsNext = false
        val mult = forPlayer.streakMultiplier
        val gained = forPlayer.xp - before
        val streakMsg = when (forPlayer.streak) {
            Player.STREAK_HOT -> "🔥 ¡RACHA x3! Multiplicador x2"
            Player.STREAK_FIRE -> "🔥🔥 ¡RACHA x5! Multiplicador x3"
            Player.STREAK_LEGEND -> "💀 ¡IMPARABLE! Multiplicador x5"
            else -> null
        }
        return ResolveResult(
            gainedXp = gained,
            newStreak = forPlayer.streak,
            multiplier = mult,
            streakMessage = streakMsg,
            levelUp = forPlayer.level > wasLevel,
            newLevel = forPlayer.level
        )
    }

    /** No pudo / no quiso (riesgo fallido resta). */
    fun resolveFail() {
        lastCard?.let { c ->
            if (c.type == CardType.RIESGO) {
                currentPlayer.points = (currentPlayer.points - 15).coerceAtLeast(0)
            }
        }
        doublePointsNext = false
        currentPlayer.registerFail()
    }

    /** Carta "todos responden": el grupo confirma y todos suman. */
    fun resolveEveryoneSuccess(): Map<String, Int> {
        val base = (lastCard?.baseXp() ?: 15).coerceAtLeast(15)
        val result = mutableMapOf<String, Int>()
        players.forEach { p ->
            val before = p.xp
            p.registerSuccess()
            p.addXpAndPoints(base)
            result[p.name] = p.xp - before
        }
        doublePointsNext = false
        return result
    }

    /** Votación del duelo: el ganador suma grande, el otro consuelo. */
    fun resolveDuel(winnerId: Int): ResolveResult {
        val winner = players.firstOrNull { it.id == winnerId } ?: currentPlayer
        val loser = players.firstOrNull { it.id != winnerId } ?: winner
        val result = resolveSuccess(winner)
        winner.addXpAndPoints(20) // bonus duelo
        loser.registerFail()
        loser.addXpAndPoints(10)
        doublePointsNext = false
        return result
    }

    /** §16 Pasar: nunca se obliga a nadie. Consume 1 paso (o comodín). */
    fun pass(free: Boolean = false): Boolean {
        val p = currentPlayer
        return if (free || p.passesLeft > 0) {
            if (!free) p.passesLeft--
            p.registerFail()
            true
        } else false
    }

    /** §17 Uso de comodín. Devuelve mensaje o null si no quedan. */
    fun useWildcard(kind: Wildcard, category: Category? = null): String? {
        val p = currentPlayer
        if (p.wildcardsLeft <= 0) return null
        p.wildcardsLeft--
        return when (kind) {
            Wildcard.CHANGE_CARD -> { "Carta cambiada 🔄" }
            Wildcard.FREE_PASS -> { p.registerFail(); "Paso gratis usado 🛡️" }
            Wildcard.CHANGE_PLAYER -> {
                val target = players.filter { it.id != p.id }.random()
                switchToPlayer(target.id)
                "Turno ahora para ${target.name} 👤"
            }
            Wildcard.DOUBLE_POINTS -> { doublePointsNext = true; "La siguiente carta vale DOBLE ✖️2" }
            Wildcard.CHOOSE_CATEGORY -> { forcedCategoryOnce = category; "Categoría: ${category?.displayName} 🎯" }
        }
    }

    private var forcedCategoryOnce: Category? = null

    fun consumeForcedCategory(): Category? {
        val c = forcedCategoryOnce; forcedCategoryOnce = null; return c
    }

    fun redealCurrent(): Deal = questionCard(cardTypeForMode(), consumeForcedCategory())

    fun switchToPlayer(id: Int) {
        val idx = players.indexOfFirst { it.id == id }
        if (idx >= 0) turnIndex = idx
    }

    fun everyoneRespondsNext(): Deal = questionCard(CardType.TODOS_RESPONDEN)

    /** Avanza el turno salvo cartas de doble turno. */
    fun advanceTurn() {
        if (lastCard?.type == CardType.DOBLE_TURNO && !keepTurn) {
            keepTurn = true
            return // se repite turno (efecto doble turno)
        }
        keepTurn = false
        turnIndex = (turnIndex + 1) % players.size
    }

    fun isEnded(): Boolean = config.maxTurns > 0 && totalTurns >= config.maxTurns

    /** Restaura una partida guardada (usado por SessionRepository). */
    fun restoreState(
        savedTurnIndex: Int,
        savedTotalTurns: Int,
        usedQuestions: Set<Int>,
        usedChallenges: Set<Int>,
        doubleNext: Boolean
    ) {
        turnIndex = savedTurnIndex
        totalTurns = savedTotalTurns
        usedQuestionIds.clear(); usedQuestionIds.addAll(usedQuestions)
        usedChallengeIds.clear(); usedChallengeIds.addAll(usedChallenges)
        doublePointsNext = doubleNext
    }

    /** Ranking final por puntos (§44). */
    fun ranking(): List<Player> = players.sortedByDescending { it.points }

    companion object {
        const val SPECIAL_EVERY = 10 // ronda especial cada 10 turnos (§43)
    }
}
