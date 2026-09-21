package com.studiolexair.preguntascalientes.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.studiolexair.preguntascalientes.audio.SoundManager
import com.studiolexair.preguntascalientes.databinding.ActivityPlayersBinding
import com.studiolexair.preguntascalientes.databinding.ItemPlayerBinding
import com.studiolexair.preguntascalientes.domain.model.GameMode
import com.studiolexair.preguntascalientes.domain.model.Player
import com.studiolexair.preguntascalientes.utils.GameSession
import com.studiolexair.preguntascalientes.utils.HapticsHelper

/**
 * Gestión de jugadores (catálogo §7-8): nombre, avatar, color,
 * pronombre opcional y estado de relación.
 */
class PlayersActivity : BaseActivity() {

    private lateinit var binding: ActivityPlayersBinding
    private lateinit var adapter: PlayersAdapter

    private var selectedAvatar = Player.AVATAR_CHOICES.first()
    private var selectedColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindParticles(com.studiolexair.preguntascalientes.R.id.particleBg, "👥", "✨", "❤️")

        binding.textModeBadge.text = GameSession.mode.title()
        buildAvatarChips()
        setupRecycler()
        setupButtons()
        updateStartButton()
    }

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

    private fun setupRecycler() {
        adapter = PlayersAdapter(
            onDelete = { player ->
                GameSession.removePlayer(player.id)
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
        binding.btnStart.isEnabled = GameSession.canStart()
        binding.btnStart.alpha = if (GameSession.canStart()) 1f else 0.45f
        binding.textStartHint.text = if (!GameSession.canStart()) GameSession.startRequirement() else ""
    }

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
            GameSession.addPlayer(
                name = name,
                hasPartner = binding.switchPartner.isChecked,
                avatar = selectedAvatar,
                colorIndex = selectedColor,
                pronoun = pronoun.ifEmpty { null }
            )
            SoundManager.play(this, SoundManager.SFX_POP)
            HapticsHelper.card(this)
            binding.editName.text?.clear()
            binding.editPronoun.text?.clear()
            refreshList()
        }

        binding.btnStart.sfxClick {
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
            textPlayerName.text = p.pronoun?.let { "${p.name} ($it)" } ?: p.name
            textPlayerStatus.text = "${p.getStatusEmoji()} ${p.getStatusText()}"
            textPlayerLevel.text = "Nivel ${p.level} · ${p.xp} XP"
            buttonDelete.setOnClickListener { onDelete(p) }
            root.alpha = 0f
            root.translationY = 30f
            root.animate().alpha(1f).translationY(0f).setDuration(250)
                .setStartDelay(holder.adapterPosition * 60L).start()
        }
    }
}
