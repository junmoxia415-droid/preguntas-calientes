package com.studiolexair.preguntascalientes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.studiolexair.preguntascalientes.utils.PartyDialog
import com.studiolexair.preguntascalientes.utils.PartyDialog.showParty
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studiolexair.preguntascalientes.audio.SoundManager
import com.studiolexair.preguntascalientes.data.db.CustomQuestionEntity
import com.studiolexair.preguntascalientes.data.db.DatabaseProvider
import com.studiolexair.preguntascalientes.databinding.ActivityCustomQuestionsBinding
import com.studiolexair.preguntascalientes.databinding.ItemCustomQuestionBinding
import com.studiolexair.preguntascalientes.domain.model.Category
import com.studiolexair.preguntascalientes.utils.HapticsHelper
import kotlinx.coroutines.launch

/**
 * Preguntas personalizadas (catálogo §19): crear, listar y borrar.
 * Se integran automáticamente al mazo (Room).
 */
class CustomQuestionsActivity : BaseActivity() {

    private lateinit var binding: ActivityCustomQuestionsBinding
    private val dao by lazy { DatabaseProvider.get(this).customQuestionDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindParticles(com.studiolexair.preguntascalientes.R.id.particleBg, "✍️", "✨", "🃏")

        binding.recyclerCustom.layoutManager = LinearLayoutManager(this)
        binding.btnBack.sfxClick { finish() }
        binding.fabAdd.sfxClick { showAddDialog() }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        lifecycleScope.launch {
            val list = dao.getAll()
            binding.textCustomCount.text = "${list.size} preguntas personalizadas"
            binding.textEmptyCustom.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerCustom.adapter = CustomAdapter(list) { item ->
                lifecycleScope.launch {
                    dao.delete(item.id)
                    SoundManager.play(this@CustomQuestionsActivity, SoundManager.SFX_WHOOSH)
                    refresh()
                }
            }
        }
    }

    private fun showAddDialog() {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 24, 48, 8)
        }
        val edit = android.widget.EditText(this).apply {
            hint = "Escribe tu pregunta... ✍️"
        }
        val spinner = android.widget.Spinner(this).apply {
            adapter = android.widget.ArrayAdapter(
                this@CustomQuestionsActivity,
                android.R.layout.simple_spinner_dropdown_item,
                Category.values().map { "${it.emoji} ${it.displayName}" }
            )
        }
        val intensity = android.widget.Spinner(this).apply {
            adapter = android.widget.ArrayAdapter(
                this@CustomQuestionsActivity,
                android.R.layout.simple_spinner_dropdown_item,
                listOf("😊 Suave", "😏 Medio", "🔥 Extremo")
            )
        }
        val target = android.widget.Spinner(this).apply {
            adapter = android.widget.ArrayAdapter(
                this@CustomQuestionsActivity,
                android.R.layout.simple_spinner_dropdown_item,
                listOf("👥 Para todos", "👫 Con pareja", "💔 Solteros")
            )
        }
        layout.addView(edit); layout.addView(spinner); layout.addView(intensity); layout.addView(target)

        PartyDialog.builder(this)
            .setTitle("✍️ Nueva pregunta")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val text = edit.text.toString().trim()
                if (text.length < 8) {
                    Toast.makeText(this, "Muy corta 😅 (mín. 8 caracteres)", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                lifecycleScope.launch {
                    dao.insert(
                        CustomQuestionEntity(
                            text = text,
                            category = Category.values()[spinner.selectedItemPosition].name,
                            intensity = intensity.selectedItemPosition + 1,
                            forPartnered = target.selectedItemPosition - 1 // 0->-1 todos, 1->0 solteros, 2->1 pareja
                        )
                    )
                    SoundManager.play(this@CustomQuestionsActivity, SoundManager.SFX_DING)
                    HapticsHelper.celebrate(this@CustomQuestionsActivity)
                    Toast.makeText(this@CustomQuestionsActivity, "✅ ¡Guardada! Saldrá en las partidas", Toast.LENGTH_SHORT).show()
                    refresh()
                }
            }
            .setNegativeButton("Cancelar", null)
            .showParty()
    }

    class CustomAdapter(
        private val items: List<CustomQuestionEntity>,
        private val onDelete: (CustomQuestionEntity) -> Unit
    ) : RecyclerView.Adapter<CustomAdapter.VH>() {
        class VH(val binding: ItemCustomQuestionBinding) : RecyclerView.ViewHolder(binding.root)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
            ItemCustomQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
        override fun getItemCount() = items.size
        override fun onBindViewHolder(holder: VH, position: Int) {
            val q = items[position]
            holder.binding.apply {
                val cat = Category.fromString(q.category) ?: Category.DIVERTIDAS
                textQuestion.text = q.text
                textMeta.text = "${cat.emoji} ${cat.displayName} · ${"🔥".repeat(q.intensity)}"
                btnDeleteQ.setOnClickListener { onDelete(q) }
            }
        }
    }
}
