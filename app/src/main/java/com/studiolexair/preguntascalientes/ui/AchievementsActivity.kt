package com.studiolexair.preguntascalientes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studiolexair.preguntascalientes.data.db.StatsRepository
import com.studiolexair.preguntascalientes.databinding.ActivityAchievementsBinding
import com.studiolexair.preguntascalientes.databinding.ItemAchievementBinding
import com.studiolexair.preguntascalientes.domain.model.Achievement
import kotlinx.coroutines.launch

/**
 * Logros (catálogo §28).
 */
class AchievementsActivity : BaseActivity() {

    private lateinit var binding: ActivityAchievementsBinding
    private val stats by lazy { StatsRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAchievementsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindParticles(com.studiolexair.preguntascalientes.R.id.particleBg, "🏆", "🏅", "✨")
        binding.recyclerAchievements.layoutManager = LinearLayoutManager(this)
        binding.btnBack.sfxClick { finish() }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val unlocked = stats.unlockedKeys().toSet()
            binding.textTotal.text = "${unlocked.size} / ${Achievement.values().size} logros desbloqueados"
            binding.recyclerAchievements.adapter = AchAdapter(unlocked)
        }
    }

    class AchAdapter(private val unlocked: Set<String>) : RecyclerView.Adapter<AchAdapter.VH>() {
        class VH(val binding: ItemAchievementBinding) : RecyclerView.ViewHolder(binding.root)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
            ItemAchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        override fun getItemCount() = Achievement.values().size
        override fun onBindViewHolder(holder: VH, position: Int) {
            val a = Achievement.values()[position]
            val has = a.key in unlocked
            holder.binding.apply {
                textEmoji.text = a.emoji
                textTitle.text = a.title
                textDesc.text = a.description
                textEmoji.alpha = if (has) 1f else 0.25f
                textStatus.text = if (has) "✅" else "🔒"
                root.alpha = if (has) 1f else 0.6f
            }
        }
    }
}
