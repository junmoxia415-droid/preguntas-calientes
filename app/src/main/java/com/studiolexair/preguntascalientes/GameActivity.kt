package com.studiolexair.preguntascalientes

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.studiolexair.preguntascalientes.databinding.ActivityGameBinding
import com.studiolexair.preguntascalientes.viewmodels.GameViewModel

/**
 * Pantalla principal de juego
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
        setupAnimations()
        setupBackPressHandler()
    }

    /**
     * Maneja el botón "atrás" mostrando un diálogo de confirmación
     * (implementación moderna con OnBackPressedDispatcher)
     */
    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
    }

    private fun setupObservers() {
        viewModel.currentPlayer.observe(this) { player ->
            player?.let {
                binding.textCurrentPlayer.text = it.name
                binding.textPlayerStatus.text = "${it.getStatusEmoji()} ${it.getStatusText()}"
                
                // Animación de cambio de jugador
                binding.cardPlayer.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(100)
                    .withEndAction {
                        binding.cardPlayer.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(200)
                            .start()
                    }
                    .start()
            }
        }

        viewModel.currentQuestion.observe(this) { question ->
            if (question != null) {
                binding.textQuestion.text = "\"${question.text}\""
                binding.textCategory.text = "${question.category.emoji} ${question.category.displayName}"
                binding.textIntensityBadge.text = when(question.intensity) {
                    1 -> "😊 Suave"
                    2 -> "😏 Medio"
                    3 -> "🔥 Extremo"
                    else -> "Medio"
                }

                // Animación de pregunta
                binding.cardQuestion.apply {
                    alpha = 0f
                    translationX = 100f
                    animate()
                        .alpha(1f)
                        .translationX(0f)
                        .setDuration(400)
                        .start()
                }
            } else {
                binding.textQuestion.text = "No hay preguntas disponibles con los filtros actuales 😅"
            }
        }

        viewModel.intensity.observe(this) { intensity ->
            binding.textGameIntensity.text = "Intensidad: ${viewModel.getIntensityLabel()}"
        }
    }

    private fun setupListeners() {
        binding.btnNextPlayer.setOnClickListener {
            viewModel.nextTurn()
        }

        binding.btnAnotherQuestion.setOnClickListener {
            viewModel.refreshAnotherQuestion()
        }

        binding.btnExitGame.setOnClickListener {
            showExitDialog()
        }
    }

    private fun setupAnimations() {
        // Animación inicial de entrada
        binding.cardQuestion.alpha = 0f
        binding.cardQuestion.translationY = 50f
        binding.cardQuestion.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(200)
            .start()

        val buttons = listOf(binding.btnNextPlayer, binding.btnAnotherQuestion, binding.btnExitGame)
        buttons.forEachIndexed { index, button ->
            button.alpha = 0f
            button.translationY = 30f
            button.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay((index * 100 + 400).toLong())
                .start()
        }
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("¿Salir del juego?")
            .setMessage("¿Estás seguro que quieres salir? Se perderá el progreso actual.")
            .setPositiveButton("Sí, salir") { _, _ ->
                finish()
            }
            .setNegativeButton("Continuar jugando", null)
            .setNeutralButton("Ir al menú") { _, _ ->
                startActivity(Intent(this, MenuActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                })
                finish()
            }
            .show()
    }
}
