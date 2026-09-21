package com.studiolexair.preguntascalientes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.studiolexair.preguntascalientes.databinding.ActivityMenuBinding
import com.studiolexair.preguntascalientes.utils.GameSession

/**
 * Menú Principal
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        // Animación de entrada de botones
        val buttons = listOf(
            binding.btnNewGame,
            binding.btnRules,
            binding.btnCredits,
            binding.btnSettings
        )

        buttons.forEachIndexed { index, button ->
            button.alpha = 0f
            button.translationY = 100f
            button.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay((index * 150).toLong() + 200)
                .start()
        }
    }

    private fun setupListeners() {
        binding.btnNewGame.setOnClickListener {
            GameSession.reset()
            startActivity(Intent(this, PlayersActivity::class.java))
        }

        binding.btnRules.setOnClickListener {
            showRulesDialog()
        }

        binding.btnCredits.setOnClickListener {
            startActivity(Intent(this, CreditsActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            showSettingsDialog()
        }
    }

    private fun showRulesDialog() {
        val rules = """
            🔥 PREGUNTAS CALIENTES - REGLAS 🔥

            📋 OBJETIVO:
            Conocerse mejor entre amigos con preguntas divertidas y atrevidas.

            🎮 CÓMO JUGAR:
            
            1️⃣ Agrega mínimo 2 jugadores
            2️⃣ Indica si tienen pareja o están solteros
            3️⃣ Selecciona categorías de preguntas
            4️⃣ Elige la intensidad (Suave/Medio/Extremo)
            5️⃣ ¡Comienza el juego!

            🔄 TURNOS:
            • Cada jugador responde en su turno
            • Puedes saltar a otra pregunta si no quieres responder
            • ¡Sé honesto y diviértete!

            ⚠️ REGLAS DE ORO:
            • Lo que pasa en el juego, se queda en el grupo 😏
            • Respeta si alguien no quiere responder
            • ¡Sin juzgar, solo divertirse!

            💡 CONSEJO:
            Mientras más extremo, ¡más divertido!
        """.trimIndent()

        MaterialAlertDialogBuilder(this)
            .setTitle("📖 Reglas del Juego")
            .setMessage(rules)
            .setPositiveButton("¡Entendido! 🎮") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showSettingsDialog() {
        val options = arrayOf("🔊 Sonidos (Próximamente)", "🌙 Tema Oscuro (Próximamente)", "🗑️ Reiniciar Datos")
        
        MaterialAlertDialogBuilder(this)
            .setTitle("⚙️ Configuración")
            .setItems(options) { dialog, which ->
                when (which) {
                    2 -> {
                        GameSession.reset()
                        android.widget.Toast.makeText(this, "Datos reiniciados", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        android.widget.Toast.makeText(this, "Función próximamente 🚀", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }
}
