package com.studiolexair.preguntascalientes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.studiolexair.preguntascalientes.databinding.ActivityCategoriesBinding
import com.studiolexair.preguntascalientes.models.Category
import com.studiolexair.preguntascalientes.viewmodels.GameViewModel

/**
 * Configurar categorías e intensidad
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
class CategoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoriesBinding
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCategoryChips()
        setupIntensitySlider()
        setupObservers()
        setupListeners()
    }

    private fun setupCategoryChips() {
        val chipGroup = binding.chipGroupCategories
        chipGroup.removeAllViews()

        Category.values().forEach { category ->
            val chip = Chip(this).apply {
                text = "${category.emoji} ${category.displayName}"
                isCheckable = true
                isClickable = true
                setChipBackgroundColorResource(R.color.chip_background_selector)
                setTextColor(resources.getColorStateList(R.color.chip_text_selector, null))
                chipStrokeWidth = 2f
                setChipStrokeColorResource(R.color.primary)
            }

            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.toggleCategory(category)
                } else {
                    viewModel.toggleCategory(category)
                }
                updateStartButtonState()
            }

            chipGroup.addView(chip)
        }
    }

    private fun setupIntensitySlider() {
        binding.sliderIntensity.apply {
            valueFrom = 1f
            valueTo = 3f
            stepSize = 1f
            value = 2f

            addOnChangeListener { _, value, _ ->
                viewModel.setIntensity(value.toInt())
            }
        }

        // Labels
        binding.textIntensityLow.text = "Suave 😊"
        binding.textIntensityHigh.text = "Extremo 🔥"
    }

    private fun setupObservers() {
        viewModel.intensity.observe(this) { intensity ->
            binding.textIntensityValue.text = viewModel.getIntensityLabel()
            binding.sliderIntensity.value = intensity.toFloat()
        }

        viewModel.selectedCategories.observe(this) { categories ->
            binding.textSelectedCount.text = "${categories.size} categorías seleccionadas"
            updateStartButtonState()
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnStartMatch.setOnClickListener {
            if (viewModel.canStartMatch()) {
                viewModel.startGame()
                startActivity(Intent(this, GameActivity::class.java))
            } else {
                Toast.makeText(this, "Selecciona al menos 1 categoría", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSelectAll.setOnClickListener {
            Category.values().forEach { category ->
                if (category !in viewModel.selectedCategories.value!!) {
                    viewModel.toggleCategory(category)
                }
            }
            // Marcar todos los chips
            for (i in 0 until binding.chipGroupCategories.childCount) {
                (binding.chipGroupCategories.getChildAt(i) as? Chip)?.isChecked = true
            }
        }

        binding.btnDeselectAll.setOnClickListener {
            viewModel.selectedCategories.value?.toList()?.forEach { category ->
                viewModel.toggleCategory(category)
            }
            // Desmarcar todos los chips
            for (i in 0 until binding.chipGroupCategories.childCount) {
                (binding.chipGroupCategories.getChildAt(i) as? Chip)?.isChecked = false
            }
        }
    }

    private fun updateStartButtonState() {
        val hasCategories = viewModel.selectedCategories.value?.isNotEmpty() == true
        binding.btnStartMatch.isEnabled = hasCategories
    }
}
