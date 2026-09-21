package com.studiolexair.preguntascalientes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.studiolexair.preguntascalientes.databinding.ItemPlayerBinding
import com.studiolexair.preguntascalientes.models.Player

/**
 * Adapter para lista de jugadores
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
class PlayersAdapter(
    private val onDeleteClick: (Player) -> Unit
) : ListAdapter<Player, PlayersAdapter.PlayerViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ItemPlayerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlayerViewHolder(
        private val binding: ItemPlayerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(player: Player) {
            binding.apply {
                textPlayerName.text = player.name
                textPlayerStatus.text = "${player.getStatusEmoji()} ${player.getStatusText()}"
                buttonDelete.setOnClickListener {
                    onDeleteClick(player)
                }
                // Animación de aparición
                root.alpha = 0f
                root.translationY = 50f
                root.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setStartDelay((adapterPosition * 100).toLong())
                    .start()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem == newItem
        }
    }
}
