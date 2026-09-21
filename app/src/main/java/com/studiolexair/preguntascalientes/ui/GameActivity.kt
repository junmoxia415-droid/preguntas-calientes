package com.studiolexair.preguntascalientes.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.studiolexair.preguntascalientes.R
import com.studiolexair.preguntascalientes.audio.SoundManager
import com.studiolexair.preguntascalientes.data.questions.QuestionRepository
import com.studiolexair.preguntascalientes.data.db.SessionRepository
import com.studiolexair.preguntascalientes.data.db.StatsRepository
import com.studiolexair.preguntascalientes.databinding.ActivityGameBinding
import com.studiolexair.preguntascalientes.domain.game.GameEngine
import com.studiolexair.preguntascalientes.domain.model.CardType
import com.studiolexair.preguntascalientes.domain.model.Category
import com.studiolexair.preguntascalientes.domain.model.GameCard
import com.studiolexair.preguntascalientes.domain.model.Player
import com.studiolexair.preguntascalientes.utils.GameIcons
import com.studiolexair.preguntascalientes.utils.GameSession
import com.studiolexair.preguntascalientes.utils.HapticsHelper
import com.studiolexair.preguntascalientes.utils.PartyDialog
import com.studiolexair.preguntascalientes.utils.PartyDialog.showParty
import com.studiolexair.preguntascalientes.utils.PrefsManager
import kotlinx.coroutines.launch

/**
 * GameActivity V2.0 — La mesa de juego (catálogo §2, §5, §11-12, §15-17, §23).
 *
 * Flujo: cuenta regresiva 3-2-1-🔥GO → carta oculta → tocar para FLIP →
 * contenido (pregunta/reto/evento) → botones contextuales → puntos/XP/racha
 * → siguiente turno. Timer opcional, comodines, paso libre, eventos y
 * duelos con votación.
 */
class GameActivity : BaseActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var engine: GameEngine
    private val stats by lazy { StatsRepository(this) }

    private var currentDeal: GameEngine.Deal? = null
    private var card: GameCard? = null
    private var flipped = false
    private var timer: CountDownTimer? = null
    private var everyoneMode = false
    private var dareMode = false
    private var duelMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val continueGame = intent.getBooleanExtra("continue", false)
        if (continueGame) {
            lifecycleScope.launch {
                val restored = SessionRepository.restore(this@GameActivity)
                if (restored != null) {
                    engine = restored
                    engine.loadContent(
                        QuestionRepository.builtinQuestions(),
                        QuestionRepository.customQuestions(this@GameActivity),
                        QuestionRepository.builtinChallenges()
                    )
                    GameSession.players = engine.players
                    setupEngine(withCountdown = true)
                } else {
                    Toast.makeText(this@GameActivity, "No se pudo continuar la partida 😢", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            engine = GameEngine(GameSession.players, GameSession.config(), PrefsManager.familiarMode(this))
            lifecycleScope.launch {
                engine.loadContent(
                    QuestionRepository.builtinQuestions(),
                    QuestionRepository.customQuestions(this@GameActivity),
                    QuestionRepository.builtinChallenges()
                )
                GameSession.engine = engine
                setupEngine(withCountdown = true)
            }
        }

        setupUI()
        setupBackPressHandler()
    }

    // ═══════════ Setup ═══════════

    private fun setupEngine(withCountdown: Boolean) {
        bindParticles(
            com.studiolexair.preguntascalientes.R.id.particleBg,
            *emojiForMode()
        )
        SoundManager.playMusic(this, GameSession.intensity)
        if (withCountdown) runIntroCountdown() else nextDeal()
    }

    private fun emojiForMode(): Array<String> = when (GameSession.intensity) {
        3 -> arrayOf("🔥", "😈", "💀", "✨")
        2 -> arrayOf("🔥", "❤️", "💫", "✨")
        else -> arrayOf("❤️", "💫", "✨", "🫧")
    }

    private fun setupUI() {
        binding.cardFrame.setOnClickListener {
            if (!flipped) flipCard()
        }
        binding.btnPositive.sfxClick { onPositive() }
        binding.btnNegative.sfxClick { onNegative() }
        binding.btnPass.sfxClick { onPass(free = false) }
        binding.btnWildcard.sfxClick { showWildcardMenu() }
        binding.btnVerdad.sfxClick { dealTruth() }
        binding.btnReto.sfxClick { dealReto() }
        binding.btnVoteA.sfxClick { finishDuel(engine.players.getOrNull(0)?.id ?: 1) }
        binding.btnVoteB.sfxClick { finishDuel(engine.players.getOrNull(1)?.id ?: 2) }
        binding.btnEndGame.sfxClick { confirmEndGame() }
    }

    // ═══════════ Cuenta regresiva 3-2-1-GO (§12) ═══════════

    private fun runIntroCountdown() {
        binding.overlayCountdown.visibility = View.VISIBLE
        val steps = listOf("READY...", "3", "2", "1", "🔥 GO!")
        var i = 0
        fun next() {
            if (i >= steps.size) {
                binding.overlayCountdown.visibility = View.GONE
                nextDeal()
                return
            }
            val s = steps[i++]
            binding.textCountdown.text = s
            binding.textCountdown.scaleX = 0.5f; binding.textCountdown.scaleY = 0.5f
            binding.textCountdown.animate().scaleX(1f).scaleY(1f).setDuration(420).start()
            if (s == "🔥 GO!") {
                SoundManager.play(this, SoundManager.SFX_GO)
                HapticsHelper.event(this)
                binding.root.postDelayed({ next() }, 550)
            } else {
                SoundManager.play(this, SoundManager.SFX_COUNT)
                HapticsHelper.tick(this)
                binding.root.postDelayed({ next() }, 650)
            }
        }
        next()
    }

    // ═══════════ Reparto y cartas ═══════════

    private fun nextDeal() {
        resetCardState()
        updateHeader()

        if (engine.isEnded()) { endGame(); return }

        val deal = engine.deal()
        if (deal.banner == "END") { endGame(); return }
        currentDeal = deal

        deal.roundTitle?.let { showBanner(it, long = true) }
        if (deal.banner != null && deal.roundTitle == null && !deal.needsPlayerPick) {
            // banners normales solo para eventos especiales (no cada carta)
            if (deal.card?.isSpecial == true) showBanner(deal.banner)
        }

        when {
            deal.truthOrDareChoice -> showTruthDareChoice()
            deal.card == null -> {
                Toast.makeText(this, "Reintentando...", Toast.LENGTH_SHORT).show()
                binding.root.postDelayed({ engine.advanceTurn(); nextDeal() }, 600)
            }
            else -> {
                card = deal.card
                onCardReady(deal)
            }
        }
    }

    private fun onCardReady(deal: GameEngine.Deal) {
        val c = deal.card ?: return
        everyoneMode = c.type == CardType.TODOS_RESPONDEN
        dareMode = c.challenge != null
        duelMode = c.type == CardType.DUELO

        // Frente de la carta (oculta) — icono SVG propio según el tipo
        binding.imgFrontIcon.setImageResource(GameIcons.forCardType(c.type))
        binding.frontEmoji.text = c.frontEmoji
        binding.frontTitle.text = if (c.isSpecial) "CARTA ESPECIAL" else engine.currentPlayer.name
        binding.frontHint.text = "TOCAR PARA DESCUBRIR"
        HapticsHelper.card(this)

        if (deal.doublePoints) showBanner("✖️2 ¡Esta carta vale DOBLE!")

        when {
            deal.needsPlayerPick -> showPlayerPicker { targetId ->
                engine.switchToPlayer(targetId)
                updateHeader()
                flipCard()
            }
        }
        updateHeader()
        // V2.1: el timer arranca SOLO al terminar el flip (ver flipCard)
    }

    // ═══════════ FLIP 3D (§11) ═══════════

    private fun flipCard() {
        if (flipped) return
        flipped = true
        SoundManager.play(this, SoundManager.SFX_FLIP)
        HapticsHelper.card(this)

        val c = card ?: return
        val out = ObjectAnimator.ofFloat(binding.cardFrame, "rotationY", 0f, 90f).setDuration(180)
        val inn = ObjectAnimator.ofFloat(binding.cardFrame, "rotationY", -90f, 0f).setDuration(220)
        out.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                binding.cardFront.visibility = View.GONE
                binding.cardBack.visibility = View.VISIBLE
                renderCardContent(c)
            }
        })
        AnimatorSet().apply {
            playSequentially(out, inn)
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    // V2.1 §15: el timer arranca SOLO cuando termina el flip
                    // (nunca con la carta boca abajo)
                    startTimerIfNeeded()
                }
            })
            start()
        }
    }

    private fun renderCardContent(c: GameCard) {
        binding.textCardType.text = c.headerText()
        binding.textCardContent.text = c.contentText()
        val st = engine.currentPlayer

        binding.textCardHint.text = when {
            c.isSpecial -> ""
            c.challenge != null -> "Reto para ${st.name} · +${c.challenge.xpReward} XP si lo hace"
            else -> "Pregunta para ${st.name} · nivel ${"🔥".repeat(c.intensityOf())}"
        }

        // Botones contextuales
        binding.rowTruthDare.visibility = View.GONE
        binding.rowDuelVote.visibility = View.GONE
        binding.rowActions.visibility = View.VISIBLE

        when {
            c.isSpecial && c.type == CardType.TODOS_RESPONDEN -> {
                binding.btnPositive.text = "✅ TODOS RESPONDIERON"
                binding.btnNegative.visibility = View.GONE
                binding.btnPass.visibility = View.GONE
            }
            c.isSpecial -> {
                binding.btnPositive.text = "👍 VALE"
                binding.btnNegative.visibility = View.GONE
            }
            dareMode -> {
                binding.btnPositive.text = "💪 LO HICE"
                binding.btnNegative.text = "😅 NO PUDE"
                binding.btnNegative.visibility = View.VISIBLE
            }
            c.type == CardType.RIESGO -> {
                binding.btnPositive.text = "😏 ME ATREVO (+2x XP)"
                binding.btnNegative.text = "💀 FALLÉ (-15)"
                binding.btnNegative.visibility = View.VISIBLE
            }
            else -> {
                binding.btnPositive.text = if (everyoneMode) "✅ TODOS RESPONDIERON" else "✅ RESPONDÍ"
                binding.btnNegative.text = "🙈 NO QUIERO DECIRLO"
                binding.btnNegative.visibility = View.VISIBLE
            }
        }
        if (duelMode) {
            binding.btnPositive.text = "🗳️ VOTAR RESULTADO"
            binding.btnNegative.visibility = View.GONE
        }
    }

    // ═══════════ Verdad o Reto ═══════════

    private fun showTruthDareChoice() {
        binding.cardFront.visibility = View.VISIBLE
        binding.cardBack.visibility = View.GONE
        binding.imgFrontIcon.setImageResource(R.drawable.ic_mask)
        binding.frontEmoji.text = "🎭"
        binding.frontTitle.text = "VERDAD O RETO"
        binding.frontHint.text = "${engine.currentPlayer.name}, elige tu destino"
        binding.rowTruthDare.visibility = View.VISIBLE
        binding.rowActions.visibility = View.GONE
        binding.rowDuelVote.visibility = View.GONE
    }

    private fun dealTruth() {
        binding.rowTruthDare.visibility = View.GONE
        val deal = engine.dealTruth()
        card = deal.card
        onCardReady(deal)
        flipCard()
    }

    private fun dealReto() {
        binding.rowTruthDare.visibility = View.GONE
        val deal = engine.dealDare()
        card = deal.card
        onCardReady(deal)
        flipCard()
    }

    // ═══════════ Resolución ═══════════

    private fun onPositive() {
        // V2.1: IMPOSIBLE responder sin haber volteado la carta
        if (!flipped) return
        val c = card
        if (c == null) { advanceAfterResolve(); return }

        when {
            duelMode -> { showDuelVote(); return }
            c.isSpecial && c.type == CardType.TODOS_RESPONDEN -> {
                val gains = engine.resolveEveryoneSuccess()
                celebrate("⚡ ${gains.entries.joinToString(", ") { "${it.key} +${it.value} XP" }}")
            }
            everyoneMode -> {
                val gains = engine.resolveEveryoneSuccess()
                celebrate("⚡ ${gains.entries.joinToString(", ") { "${it.key} +${it.value} XP" }}")
            }
            c.isSpecial -> {
                SoundManager.play(this, SoundManager.SFX_DING)
            }
            else -> {
                val result = engine.resolveSuccess()
                SoundManager.play(this, if (result.streakMessage != null) SoundManager.SFX_FANFARE else SoundManager.SFX_DING)
                HapticsHelper.celebrate(this)
                if (result.streakMessage != null) showBanner(result.streakMessage)
                if (result.levelUp) {
                    showBanner("🏅 ¡${engine.currentPlayer.name} sube a NIVEL ${result.newLevel}!")
                    binding.confettiView.burst(90)
                }
                statsTick(c)
                celebrate("+${result.gainedXp} XP")
            }
        }
        markDiscovered(c)
        advanceAfterResolve()
    }

    private fun onNegative() {
        // V2.1: imposible resolver sin flip
        if (!flipped) return
        engine.resolveFail()
        HapticsHelper.fail(this)
        SoundManager.play(this, SoundManager.SFX_WHOOSH)
        celebrate("Racha perdida ❄️", fail = true)
        advanceAfterResolve()
    }

    private fun onPass(free: Boolean) {
        // V2.1: imposible pasar sin flip (no se gasta paso a ciegas)
        if (!free && !flipped) return
        val p = engine.currentPlayer
        if (engine.pass(free)) {
            SoundManager.play(this, SoundManager.SFX_WHOOSH)
            HapticsHelper.fail(this)
            Toast.makeText(this, "🛡️ ${p.name} pasa (${p.passesLeft} restantes)", Toast.LENGTH_SHORT).show()
            markDiscovered(card)
            advanceAfterResolve()
        } else {
            Toast.makeText(this, "❌ No te quedan pasos. Usa un comodín 🃏", Toast.LENGTH_SHORT).show()
            HapticsHelper.fail(this)
        }
    }

    /** Duelo: votación A/B. */
    private fun showDuelVote() {
        binding.rowActions.visibility = View.GONE
        binding.rowDuelVote.visibility = View.VISIBLE
        val a = engine.players.getOrNull(0)?.name ?: "A"
        val b = engine.players.getOrNull(1)?.name ?: "B"
        binding.btnVoteA.text = "👈 ${a}"
        binding.btnVoteB.text = "${b} 👉"
    }

    private fun finishDuel(winnerId: Int) {
        val result = engine.resolveDuel(winnerId)
        SoundManager.play(this, SoundManager.SFX_FANFARE)
        HapticsHelper.celebrate(this)
        binding.confettiView.burst(110)
        val winner = engine.players.firstOrNull { it.id == winnerId }?.name ?: "?"
        celebrate("⚔️ ¡${winner} gana el duelo! +${result.gainedXp} XP")
        markDiscovered(card)
        advanceAfterResolve()
    }

    /** Graba estadísticas (§27-28). */
    private fun statsTick(c: GameCard) {
        lifecycleScope.launch {
            if (c.challenge != null) stats.increment(StatsRepository.KEY_DARES)
            else stats.increment(StatsRepository.KEY_QUESTIONS)
            if (c.intensityOf() == 3) stats.increment(StatsRepository.KEY_EXTREMES)
            stats.setMax(StatsRepository.KEY_MAX_STREAK, engine.currentPlayer.maxStreak.toLong())
        }
    }

    private fun markDiscovered(c: GameCard?) {
        c?.question?.let {
            QuestionRepository.markDiscovered(this, it.id)
        }
    }

    private fun advanceAfterResolve() {
        stopTimer()
        binding.root.postDelayed({
            engine.advanceTurn()
            nextDeal()
            saveSessionSoft()
        }, 900)
    }

    private fun resetCardState() {
        flipped = false
        everyoneMode = false
        dareMode = false
        duelMode = false
        binding.cardBack.visibility = View.GONE
        binding.cardFront.visibility = View.VISIBLE
        // V2.1: las acciones permanecen ocultas hasta el flip
        binding.rowActions.visibility = View.GONE
        binding.rowTruthDare.visibility = View.GONE
        binding.rowDuelVote.visibility = View.GONE
        binding.btnNegative.visibility = View.VISIBLE
        binding.btnPass.visibility = View.VISIBLE
    }

    // ═══════════ HUD ═══════════

    private fun updateHeader() {
        val p = engine.currentPlayer
        binding.textAvatar.text = p.avatar
        // V2.1: círculo del avatar teñido con el color del jugador
        binding.textAvatar.background = PlayersAdapter.tintedCircle(binding.root, p)
        binding.textPlayerName.text = p.name
        binding.textPlayerStatus.text = "${p.getStatusEmoji()} ${p.getStatusText()} · Nivel ${p.level}"
        binding.textPoints.text = "⭐ ${p.points}"
        binding.textStreak.text = if (p.streak > 0) "🔥 x${p.streak}" else ""
        binding.textStreak.visibility = if (p.streak > 0) View.VISIBLE else View.GONE
        binding.textPasses.text = "🛡️ ${p.passesLeft}"
        binding.textWildcards.text = "🃏 ${p.wildcardsLeft}"
        binding.textRound.text = "Turno ${engine.totalTurns + 1}"
        binding.textModeBadge.text = GameSession.mode.title()

        // Animación rotación avatar (§12 cambio de jugador)
        binding.textAvatar.animate().rotationBy(360f).setDuration(450).start()
    }

    // ═══════════ Timer (§15) ═══════════

    private fun startTimerIfNeeded() {
        stopTimer()
        val secs = GameSession.timerSeconds
        if (secs <= 0) {
            binding.timerZone.visibility = View.GONE
            return
        }
        binding.timerZone.visibility = View.VISIBLE
        binding.timerProgress.max = secs * 10
        timer = object : CountDownTimer(secs * 1000L, 100) {
            override fun onTick(ms: Long) {
                val left = (ms / 100).toInt()
                binding.timerProgress.progress = left
                binding.textTimer.text = String.format("0:%02d", ms / 1000)
                if (ms in 3001..4100 && ms % 1000 < 150) {
                    SoundManager.play(this@GameActivity, SoundManager.SFX_TICK)
                    HapticsHelper.tick(this@GameActivity)
                }
            }
            override fun onFinish() {
                binding.textTimer.text = "0:00"
                Toast.makeText(this@GameActivity, "⏰ ¡Tiempo agotado!", Toast.LENGTH_SHORT).show()
                onNegative()
            }
        }.start()
    }

    private fun stopTimer() { timer?.cancel(); timer = null }

    // ═══════════ Menú de comodines (§17) ═══════════

    private fun showWildcardMenu() {
        val p = engine.currentPlayer
        if (p.wildcardsLeft <= 0) {
            Toast.makeText(this, "❌ Sin comodines (${p.name})", Toast.LENGTH_SHORT).show()
            HapticsHelper.fail(this)
            return
        }
        val kinds = GameEngine.Wildcard.values()
        val labels = kinds.map { "${it.emoji} ${it.label}" }.toTypedArray()
        PartyDialog.builder(this)
            .setTitle("🃏 Comodines de ${p.name} (${p.wildcardsLeft})")
            .setItems(labels) { _, which ->
                val kind = kinds[which]
                when (kind) {
                    GameEngine.Wildcard.CHANGE_CARD -> {
                        engine.useWildcard(kind)
                        SoundManager.play(this, SoundManager.SFX_FLIP)
                        val deal = engine.redealCurrent()
                        card = deal.card
                        resetCardState()
                        card?.let { onCardReady(deal) }
                    }
                    GameEngine.Wildcard.FREE_PASS -> {
                        engine.useWildcard(kind)
                        Toast.makeText(this, "🛡️ Paso gratis", Toast.LENGTH_SHORT).show()
                        markDiscovered(card)
                        advanceAfterResolve()
                    }
                    GameEngine.Wildcard.DOUBLE_POINTS -> {
                        val msg = engine.useWildcard(kind)
                        Toast.makeText(this, msg ?: "", Toast.LENGTH_SHORT).show()
                        updateHeader()
                    }
                    GameEngine.Wildcard.CHANGE_PLAYER -> {
                        val msg = engine.useWildcard(kind)
                        Toast.makeText(this, msg ?: "", Toast.LENGTH_SHORT).show()
                        updateHeader()
                    }
                    GameEngine.Wildcard.CHOOSE_CATEGORY -> showCategoryPicker {
                        val msg = engine.useWildcard(kind, it)
                        Toast.makeText(this, msg ?: "", Toast.LENGTH_SHORT).show()
                        val deal = engine.redealCurrent()
                        card = deal.card
                        resetCardState()
                        card?.let { onCardReady(deal) }
                    }
                }
            }
            .showParty()
    }

    private fun showCategoryPicker(onPick: (Category) -> Unit) {
        val cats = Category.visible(PrefsManager.familiarMode(this))
        PartyDialog.builder(this)
            .setTitle("🎯 Elige categoría")
            .setItems(cats.map { "${it.emoji} ${it.displayName}" }.toTypedArray()) { _, i -> onPick(cats[i]) }
            .showParty()
    }

    private fun showPlayerPicker(onPick: (Int) -> Unit) {
        val others = engine.players.filter { it.id != engine.currentPlayer.id }
        PartyDialog.builder(this)
            .setTitle("🎯 ¿Quién responde?")
            .setItems(others.map { "${it.avatar} ${it.name}" }.toTypedArray()) { _, i -> onPick(others[i].id) }
            .setCancelable(false)
            .showParty()
    }

    // ═══════════ Efectos ═══════════

    private fun showBanner(text: String, long: Boolean = false) {
        binding.textBanner.text = text
        binding.textBanner.visibility = View.VISIBLE
        binding.textBanner.alpha = 0f; binding.textBanner.scaleX = 0.7f; binding.textBanner.scaleY = 0.7f
        binding.textBanner.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(300).start()
        SoundManager.play(this, SoundManager.SFX_EVENT)
        HapticsHelper.event(this)
        binding.textBanner.postDelayed({
            binding.textBanner.animate().alpha(0f).setDuration(250)
                .withEndAction { binding.textBanner.visibility = View.GONE }.start()
        }, if (long) 2600 else 1700)
    }

    private fun celebrate(text: String, fail: Boolean = false) {
        if (!fail) binding.confettiView.burst(60)
        binding.textFloatXp.text = text
        binding.textFloatXp.visibility = View.VISIBLE
        binding.textFloatXp.alpha = 1f
        binding.textFloatXp.translationY = 0f
        binding.textFloatXp.animate()
            .translationY(-120f).alpha(0f).setDuration(1100)
            .withEndAction { binding.textFloatXp.visibility = View.GONE }
            .start()
    }

    // ═══════════ Fin de partida (§44) ═══════════

    private fun confirmEndGame() {
        PartyDialog.builder(this)
            .setTitle("🏁 Terminar partida")
            .setMessage("¿Terminar y ver los resultados?")
            .setPositiveButton("Ver resultados 🏆") { _, _ -> endGame() }
            .setNegativeButton("Guardar y salir 💾") { _, _ ->
                lifecycleScope.launch {
                    SessionRepository.save(this@GameActivity, engine, PrefsManager.familiarMode(this@GameActivity))
                    SoundManager.stopMusic()
                    finish()
                }
            }
            .setNeutralButton("Seguir jugando", null)
            .showParty()
    }

    private fun endGame() {
        stopTimer()
        SoundManager.play(this, SoundManager.SFX_FANFARE)
        HapticsHelper.celebrate(this)
        lifecycleScope.launch {
            // Persistir stats + logros + limpiar partida guardada
            engine.players.forEach { stats.recordPlayer(it) }
            stats.increment(StatsRepository.KEY_GAMES)
            val discovered = QuestionRepository.totalDiscovered(this@GameActivity).first
            val newAchievements = stats.checkEndgameAchievements(engine.players, discovered)
            SessionRepository.clear(this@GameActivity)

            SoundManager.playMusic(this@GameActivity, 0)
            val intent = Intent(this@GameActivity, ResultsActivity::class.java).apply {
                putParcelableArrayListExtra("players", ArrayList(engine.ranking()))
                putStringArrayListExtra("new_achievements",
                    ArrayList(newAchievements.map { "${it.emoji} ${it.title}: ${it.description}" }))
            }
            startActivity(intent)
            finish()
        }
    }

    private fun saveSessionSoft() {
        lifecycleScope.launch {
            SessionRepository.save(this@GameActivity, engine, PrefsManager.familiarMode(this@GameActivity))
        }
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!::engine.isInitialized) { finish(); return }
                PartyDialog.builder(this@GameActivity)
                    .setTitle("❌ Salir de la partida")
                    .setMessage("¿Estás seguro? Puedes guardar la partida y continuar luego.")
                    .setPositiveButton("Sí, salir (guardar) 💾") { _, _ ->
                        lifecycleScope.launch {
                            SessionRepository.save(this@GameActivity, engine, PrefsManager.familiarMode(this@GameActivity))
                            SoundManager.stopMusic()
                            finish()
                        }
                    }
                    .setNegativeButton("Continuar jugando ▶️", null)
                    .setNeutralButton("🏠 Menú") { _, _ ->
                        lifecycleScope.launch {
                            SessionRepository.save(this@GameActivity, engine, PrefsManager.familiarMode(this@GameActivity))
                            SoundManager.stopMusic()
                            finish()
                        }
                    }
                    .showParty()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
        SoundManager.pauseMusic()
    }

    override fun onResume() {
        super.onResume()
        SoundManager.resumeMusic(this)
        if (flipped) startTimerIfNeeded()
    }

    override fun onDestroy() {
        stopTimer()
        super.onDestroy()
    }
}
