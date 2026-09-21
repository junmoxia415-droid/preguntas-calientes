package com.studiolexair.preguntascalientes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studiolexair.preguntascalientes.data.questions.QuestionRepository
import com.studiolexair.preguntascalientes.databinding.ActivityCollectionBinding
import com.studiolexair.preguntascalientes.databinding.ItemCollectionBinding
import com.studiolexair.preguntascalientes.domain.model.Category
import kotlinx.coroutines.launch

/**
 * Colección (catálogo §18): progreso de preguntas descubiertas por pack.
 */
class CollectionActivity : BaseActivity() {

    private lateinit var binding: ActivityCollectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindParticles(com.studiolexair.preguntascalientes.R.id.particleBg, "📚", "✨", "🃏")

        binding.recyclerPacks.layoutManager = LinearLayoutManager(this)
        binding.btnBack.sfxClick { finish() }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val (seen, total) = QuestionRepository.totalDiscovered(this@CollectionActivity)
            val custom = com.studiolexair.preguntascalientes.data.db.DatabaseProvider
                .get(this@CollectionActivity).customQuestionDao().count()
            binding.textTotal.text = "Preguntas descubiertas: $seen / $total  ·  ✍️ $custom personalizadas"
            binding.progressTotal.max = total
            binding.progressTotal.progress = seen

            binding.recyclerPacks.adapter = PacksAdapter(
                QuestionRepository.getPacks().map { pack ->
                    val (s, t) = QuestionRepository.packProgress(this@CollectionActivity, pack.category)
                    Triple(pack.category, s, t)
                }
            )
        }
    }

    class PacksAdapter(private val items: List<Triple<Category, Int, Int>>) :
        RecyclerView.Adapter<PacksAdapter.VH>() {
        class VH(val binding: ItemCollectionBinding) : RecyclerView.ViewHolder(binding.root)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
            ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        override fun getItemCount() = items.size
        override fun onBindViewHolder(holder: VH, position: Int) {
            val (cat, seen, total) = items[position]
            holder.binding.apply {
                textPackName.text = "${cat.emoji} ${cat.displayName}"
                textPackCount.text = "$seen / $total"
                progressPack.max = total
                progressPack.progress = seen
            }
        }
    }
}
