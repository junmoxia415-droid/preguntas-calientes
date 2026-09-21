package com.studiolexair.preguntascalientes.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.studiolexair.preguntascalientes.databinding.ActivityModesBinding
import com.studiolexair.preguntascalientes.databinding.ItemModeBinding
import com.studiolexair.preguntascalientes.domain.model.GameMode
import com.studiolexair.preguntascalientes.utils.GameIcons
import com.studiolexair.preguntascalientes.utils.GameSession

/**
 * Selector de modos de juego V2.1 (catálogo §1 y §24).
 * V2.1: usa los iconos SVG propios (GameIcons) y respeta la
 * PLANTILLA PERSISTENTE de jugadores — elegir modo NUNCA la borra.
 */
class ModesActivity : BaseActivity() {

    private lateinit var binding: ActivityModesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindParticles(com.studiolexair.preguntascalientes.R.id.particleBg, "🎮", "✨", "🃏")

        binding.recyclerModes.layoutManager = LinearLayoutManager(this)
        binding.recyclerModes.adapter = ModesAdapter { mode ->
            // V2.1: cambiar de modo conserva la plantilla de jugadores
            GameSession.mode = mode
            GameSession.engine = null
            GameSession.continueAvailable = false
            val next = when {
                mode == GameMode.ALEATORIO -> Intent(this, RouletteActivity::class.java)
                else -> Intent(this, PlayersActivity::class.java)
            }
            startActivity(next)
        }.also { it.submitList(GameMode.values().toList()) }

        binding.btnBack.sfxClick { finish() }

        // Entrada escalonada
        binding.recyclerModes.alpha = 0f
        binding.recyclerModes.translationY = 50f
        binding.recyclerModes.animate().alpha(1f).translationY(0f).setDuration(400).start()
    }
}

class ModesAdapter(
    private val onClick: (GameMode) -> Unit
) : ListAdapter<GameMode, ModesAdapter.VH>(object : DiffUtil.ItemCallback<GameMode>() {
    override fun areItemsTheSame(a: GameMode, b: GameMode) = a == b
    override fun areContentsTheSame(a: GameMode, b: GameMode) = a == b
}) {

    class VH(val binding: ItemModeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemModeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val mode = getItem(position)
        holder.binding.apply {
            // V2.1: icono SVG propio del juego
            imgModeIcon.setImageResource(GameIcons.forMode(mode))
            textModeName.text = mode.title()
            textModeDesc.text = mode.description
            root.setOnClickListener {
                root.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80).withEndAction {
                    root.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
                }.start()
                onClick(mode)
            }
            root.alpha = 0f
            root.animate().alpha(1f).setStartDelay(holder.adapterPosition * 45L).setDuration(250).start()
        }
    }
}
