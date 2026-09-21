package com.studiolexair.preguntascalientes.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.studiolexair.preguntascalientes.R
import com.studiolexair.preguntascalientes.audio.SoundManager
import com.studiolexair.preguntascalientes.databinding.ActivityPlayersBinding
import com.studiolexair.preguntascalientes.databinding.ItemPlayerBinding
import com.studiolexair.preguntascalientes.domain.model.GameMode
import com.studiolexair.preguntascalientes.domain.model.Player
import com.studiolexair.preguntascalientes.utils.GameSession
import com.studiolexair.preguntascalientes.utils.HapticsHelper

/**
 * Gestión de jugadores V2.1 (catálogo §7-8):
 * - PLANTILLA PERSISTENTE: se puede abrir desde el MENÚ PRINCIPAL
 *   (roster=true → "Mi equipo") y aparece pre-cargada en cada modo.
 * - Vínculo de pareja: elegir un jugador existente del spinner o
 *   escribir su nombre (se vincula bidireccionalmente al agregarse).
 * - 60 avatares, color, pronombre opcional.
 */
class PlayersActivity : BaseActivity() {

    private lateinit var binding: ActivityPlayersBinding
    private lateinit var adapter: PlayersAdapter
    private var rosterMode = false

    private var selectedAvatar = Player.AVATAR_CHOICES.first()
    private var selectedColor = 0

    companion object { private const val WRITE_NEW = "✏️ Escribir nombre..." }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindParticles(R.id.particleBg, "👥", "✨", "❤️")

        rosterMode = intent.getBooleanExtra("roster", false)
        applyRosterMode()

        buildAvatarChips()
        setupPartnerUi()
        setupRecycler()
        setupButtons()
        updateStartButton()
    }

    private fun applyRosterMode() {
        // V2.1: modo "Mi equipo" (desde el menú principal)
        if (rosterMode) {
            binding.textTitle.text = "👥 Mi equipo"
            binding.textModeBadge.visibility = View.GONE
            binding.btnStart.text = "🎮 ELEGIR MODO"
        } else {
            binding.textTitle.text = "👥 Jugadores"
            binding.textModeBadge.visibility = View.VISIBLE
            binding.textModeBadge.text = GameSession.mode.title()
            binding.btnStart.text = "🎮 COMENZAR"
        }
    }

    // ═══════════ Avatar (60 opciones) ═══════════

    private fun buildAvatarChips() {
        binding.layoutAvatars.removeAllViews()
        Player.AVATAR_CHOICES.forEachIndexed { i, emoji ->
            val tv = android.widget.TextView(this).apply {
                text = emoji
                textSize = 26f
                setPadding(14, 10, 14, 10)
                setOnClickListener {
                    selectedAvatar = emoji
                    selectedColor = i % Player.PLAYER_COLORS.size
                    SoundManager.click(this@PlayersActivity)
                    HapticsHelper.tap(this@PlayersActivity)
                    updateAvatarSelection(this)
                }
            }
            binding.layoutAvatars.addView(tv)
        }
        // Preseleccionar el primero visualmente
        if (binding.layoutAvatars.childCount > 0) {
            val first = binding.layoutAvatars.getChildAt(0) as? android.widget.TextView
            for (i in 0 until binding.layoutAvatars.childCount)
                binding.layoutAvatars.getChildAt(i).alpha = 0.45f
            first?.alpha = 1f
            selectedAvatar = Player.AVATAR_CHOICES.first()
            selectedColor = 0
        }
    }

    private fun updateAvatarSelection(selected: android.widget.TextView) {
        for (i in 0 until binding.layoutAvatars.childCount) {
            binding.layoutAvatars.getChildAt(i).alpha = 0.45f
        }
        selected.alpha = 1f
        selected.animate().scaleX(1.25f).scaleY(1.25f).setDuration(120).withEndAction {
            selected.animate().scaleX(1.1f).scaleY(1.1f).setDuration(80).start()
        }.start()
    }

    // ═══════════ Vínculo de pareja (spinner o texto) ═══════════

    private fun setupPartnerUi() {
        binding.switchPartner.setOnCheckedChangeListener { _, on ->
            SoundManager.click(this)
            binding.containerPartner.visibility = if (on) View.VISIBLE else View.GONE
            if (on) refreshPartnerSpinner()
        }
        binding.spinnerPartner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                val writing = binding.spinnerPartner.selectedItem?.toString() == WRITE_NEW
                binding.editPartnerName.visibility = if (writing) View.VISIBLE else View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun refreshPartnerSpinner() {
        val options = mutableListOf(WRITE_NEW) + GameSession.players.map { it.name }
        val current = binding.spinnerPartner.selectedItem?.toString()
        binding.spinnerPartner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, options
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        if (current != null && options.contains(current)) {
            binding.spinnerPartner.setSelection(options.indexOf(current))
        }
    }

    /** Pareja elegida: jugador existente (spinner) o nombre a mano. */
    private fun partnerChosen(): String? {
        if (!binding.switchPartner.isChecked) return null
        val sel = binding.spinnerPartner.selectedItem?.toString()
        return if (sel != null && sel != WRITE_NEW) sel
        else binding.editPartnerName.text.toString().trim().ifEmpty { null }
    }

    // ═══════════ Lista ═══════════

    private fun setupRecycler() {
        adapter = PlayersAdapter(
            onDelete = { player ->
                GameSession.removePlayer(player.id)
                GameSession.saveRoster(this)
                SoundManager.play(this, SoundManager.SFX_WHOOSH)
                refreshList()
            }
        )
        binding.recyclerPlayers.layoutManager = LinearLayoutManager(this)
        binding.recyclerPlayers.adapter = adapter
        refreshList()
    }

    private fun refreshList() {
        adapter.submitList(GameSession.players.toList())
        binding.textEmpty.visibility = if (GameSession.players.isEmpty()) View.VISIBLE else View.GONE
        binding.textCount.text = "${GameSession.players.size} jugador(es)"
        updateStartButton()
    }

    private fun updateStartButton() {
        val ok = if (rosterMode) GameSession.players.size >= 2 else GameSession.canStart()
        binding.btnStart.isEnabled = ok
        binding.btnStart.alpha = if (ok) 1f else 0.45f
        binding.textStartHint.text = when {
            ok -> ""
            rosterMode -> "Agrega al menos 2 jugadores a tu equipo"
            else -> GameSession.startRequirement()
        }
    }

    // ═══════════ Botones ═══════════

    private fun setupButtons() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnAdd.sfxClick {
            val name = binding.editName.text.toString().trim()
            val pronoun = binding.editPronoun.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Escribe un nombre primero 😅", Toast.LENGTH_SHORT).show()
                HapticsHelper.fail(this)
                return@sfxClick
            }
            if (GameSession.players.any { it.name.equals(name, true) }) {
                Toast.makeText(this, "Ya hay un jugador con ese nombre", Toast.LENGTH_SHORT).show()
                HapticsHelper.fail(this)
                return@sfxClick
            }
            val partner = partnerChosen()
            if (partner != null && partner.equals(name, true)) {
                Toast.makeText(this, "¡No puede ser pareja de sí mismo! 😂", Toast.LENGTH_SHORT).show()
                HapticsHelper.fail(this)
                return@sfxClick
            }
            GameSession.addPlayer(
                name = name,
                hasPartner = binding.switchPartner.isChecked,
                avatar = selectedAvatar,
                colorIndex = selectedColor,
                pronoun = pronoun.ifEmpty { null },
                partnerName = partner
            )
            GameSession.saveRoster(this)
            SoundManager.play(this, SoundManager.SFX_POP)
            HapticsHelper.card(this)
            binding.editName.text?.clear()
            binding.editPronoun.text?.clear()
            binding.editPartnerName.text?.clear()
            binding.switchPartner.isChecked = false
            refreshPartnerSpinner()
            refreshList()
        }

        binding.btnStart.sfxClick {
            if (rosterMode) {
                if (GameSession.players.size < 2) {
                    Toast.makeText(this, "Agrega al menos 2 jugadores 👀", Toast.LENGTH_SHORT).show()
                    HapticsHelper.fail(this)
                    return@sfxClick
                }
                GameSession.resetMatch()
                startActivity(Intent(this, ModesActivity::class.java))
                finish()
                return@sfxClick
            }
            if (!GameSession.canStart()) {
                Toast.makeText(this, GameSession.startRequirement(), Toast.LENGTH_SHORT).show()
                HapticsHelper.fail(this)
                return@sfxClick
            }
            HapticsHelper.event(this)
            // Modo aleatorio configura solo; el resto va a categorías
            if (GameSession.mode == GameMode.ALEATORIO) {
                randomizeConfig()
                startActivity(Intent(this, GameActivity::class.java))
            } else {
                startActivity(Intent(this, CategoriesActivity::class.java))
            }
        }
    }

    /** Modo aleatorio: categorías e intensidad al azar (🎲). */
    private fun randomizeConfig() {
        val cats = com.studiolexair.preguntascalientes.domain.model.Category.values().toMutableList()
        cats.shuffle()
        GameSession.selectedCategories = cats.take(4 + (0..2).random()).toMutableSet()
        GameSession.intensity = (1..3).random()
    }
}

class PlayersAdapter(
    private val onDelete: (Player) -> Unit
) : ListAdapter<Player, PlayersAdapter.VH>(object : DiffUtil.ItemCallback<Player>() {
    override fun areItemsTheSame(a: Player, b: Player) = a.id == b.id
    override fun areContentsTheSame(a: Player, b: Player) = a == b
}) {

    class VH(val binding: ItemPlayerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = getItem(position)
        holder.binding.apply {
            textAvatar.text = p.avatar
            // Círculo de avatar teñido con el color del jugador
            textAvatar.background = tintedCircle(root, p)
            textPlayerName.text = p.pronoun?.let { "${p.name} ($it)" } ?: p.name
            textPlayerStatus.text = "${p.getStatusEmoji()} ${p.getStatusText()}"
            val link = p.partnerLabel()
            textPartner.visibility = if (link != null) View.VISIBLE else View.GONE
            textPartner.text = link ?: ""
            textPlayerLevel.text = "Nivel ${p.level} · ${p.xp} XP"
            buttonDelete.setOnClickListener { onDelete(p) }
            root.alpha = 0f
            root.translationY = 30f
            root.animate().alpha(1f).translationY(0f).setDuration(250)
                .setStartDelay(holder.adapterPosition * 60L).start()
        }
    }

    companion object {
        fun tintedCircle(view: View, p: Player): Drawable? {
            val d = AppCompatResources.getDrawable(view.context, R.drawable.circle_bg)?.mutate()
            d?.let { DrawableCompat.setTint(DrawableCompat.wrap(it), Player.colorFor(p.colorIndex).toInt()) }
            return d
        }
    }
}
