package com.studiolexair.preguntascalientes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.studiolexair.preguntascalientes.adapters.PlayersAdapter
import com.studiolexair.preguntascalientes.databinding.ActivityPlayersBinding
import com.studiolexair.preguntascalientes.viewmodels.GameViewModel

/**
 * Pantalla para agregar jugadores
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
class PlayersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayersBinding
    private val viewModel: GameViewModel by viewModels()
    private lateinit var playersAdapter: PlayersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        playersAdapter = PlayersAdapter { player ->
            viewModel.removePlayer(player.id)
        }

        binding.recyclerPlayers.apply {
            layoutManager = LinearLayoutManager(this@PlayersActivity)
            adapter = playersAdapter
        }
    }

    private fun setupObservers() {
        viewModel.players.observe(this) { players ->
            playersAdapter.submitList(players)
            binding.textPlayerCount.text = "${players.size} jugador(es)"
            binding.btnStartGame.isEnabled = players.size >= 2
            
            if (players.isEmpty()) {
                binding.textEmptyList.visibility = android.view.View.VISIBLE
                binding.recyclerPlayers.visibility = android.view.View.GONE
            } else {
                binding.textEmptyList.visibility = android.view.View.GONE
                binding.recyclerPlayers.visibility = android.view.View.VISIBLE
            }
        }

        viewModel.gameMessage.observe(this) { message ->
            if (message.isNotBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAddPlayer.setOnClickListener {
            addPlayer()
        }

        binding.btnStartGame.setOnClickListener {
            if (viewModel.canStartGame()) {
                startActivity(Intent(this, CategoriesActivity::class.java))
            } else {
                Toast.makeText(this, "Necesitas mínimo 2 jugadores", Toast.LENGTH_SHORT).show()
            }
        }

        // Permitir agregar con Enter
        binding.editPlayerName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                addPlayer()
                true
            } else {
                false
            }
        }
    }

    private fun addPlayer() {
        val name = binding.editPlayerName.text.toString().trim()
        val hasPartner = binding.radioHasPartner.isChecked

        if (viewModel.addPlayer(name, hasPartner)) {
            binding.editPlayerName.text?.clear()
            binding.editPlayerName.requestFocus()
        }
    }
}
