package com.studiolexair.preguntascalientes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studiolexair.preguntascalientes.data.db.PlayerStatEntity
import com.studiolexair.preguntascalientes.data.db.StatsRepository
import com.studiolexair.preguntascalientes.databinding.ActivityStatsBinding
import com.studiolexair.preguntascalientes.databinding.ItemResultBinding
import com.studiolexair.preguntascalientes.domain.model.LevelSystem
import kotlinx.coroutines.launch

/**
 * Estadísticas globales y por jugador (catálogo §27).
 */
class StatsActivity : BaseActivity() {

    private lateinit var binding: ActivityStatsBinding
    private val stats by lazy { StatsRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindParticles(com.studiolexair.preguntascalientes.R.id.particleBg, "📊", "✨")
        binding.recyclerPlayers.layoutManager = LinearLayoutManager(this)
        binding.btnBack.sfxClick { finish() }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val counters = stats.allCounters()
            binding.textGames.text = "🎮 ${counters[StatsRepository.KEY_GAMES] ?: 0}"
            binding.textQuestions.text = "💬 ${counters[StatsRepository.KEY_QUESTIONS] ?: 0}"
            binding.textDares.text = "💪 ${counters[StatsRepository.KEY_DARES] ?: 0}"
            binding.textExtremes.text = "😈 ${counters[StatsRepository.KEY_EXTREMES] ?: 0}"
            binding.textBestStreak.text = "🔥 x${counters[StatsRepository.KEY_MAX_STREAK] ?: 0}"
            val discovered = com.studiolexair.preguntascalientes.data.questions
                .QuestionRepository.totalDiscovered(this@StatsActivity)
            binding.textDiscovered.text = "📚 ${discovered.first}/${discovered.second}"
            binding.recyclerPlayers.adapter = PlayersStatsAdapter(stats.allPlayerStats())
        }
    }

    class PlayersStatsAdapter(private val items: List<PlayerStatEntity>) :
        RecyclerView.Adapter<PlayersStatsAdapter.VH>() {
        class VH(val binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
            ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        override fun getItemCount() = items.size
        override fun onBindViewHolder(holder: VH, position: Int) {
            val p = items[position]
            holder.binding.apply {
                textMedal.text = "${position + 1}."
                textName.text = p.name
                textScore.text = "${p.xp} XP · Nv.${LevelSystem.levelForXp(p.xp)}"
                textDetail.text = "🎮 ${p.games} partidas · ${p.answered} respuestas · ${p.dares} retos · racha x${p.maxStreak}"
            }
        }
    }
}
