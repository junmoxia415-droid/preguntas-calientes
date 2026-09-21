package com.studiolexair.preguntascalientes.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.studiolexair.preguntascalientes.models.Category
import com.studiolexair.preguntascalientes.models.Player
import com.studiolexair.preguntascalientes.models.Question
import com.studiolexair.preguntascalientes.utils.GameSession
import com.studiolexair.preguntascalientes.utils.QuestionDatabase

/**
 * ViewModel principal del juego - Arquitectura MVVM
 * Desarrollado por Airien Yolexis Rojas Roque - Studio Lexair
 */
class GameViewModel : ViewModel() {

    private val _players = MutableLiveData<List<Player>>(GameSession.players)
    val players: LiveData<List<Player>> = _players

    private val _currentPlayer = MutableLiveData<Player?>()
    val currentPlayer: LiveData<Player?> = _currentPlayer

    private val _currentQuestion = MutableLiveData<Question?>()
    val currentQuestion: LiveData<Question?> = _currentQuestion

    private val _selectedCategories = MutableLiveData<Set<Category>>(GameSession.selectedCategories)
    val selectedCategories: LiveData<Set<Category>> = _selectedCategories

    private val _intensity = MutableLiveData<Int>(GameSession.intensity)
    val intensity: LiveData<Int> = _intensity

    private val _gameMessage = MutableLiveData<String>()
    val gameMessage: LiveData<String> = _gameMessage

    init {
        updateCurrentPlayer()
        loadNewQuestion()
    }

    fun addPlayer(name: String, hasPartner: Boolean): Boolean {
        if (name.isBlank()) {
            _gameMessage.value = "El nombre no puede estar vacío"
            return false
        }
        if (GameSession.players.any { it.name.equals(name.trim(), ignoreCase = true) }) {
            _gameMessage.value = "Ya existe un jugador con ese nombre"
            return false
        }
        GameSession.addPlayer(name, hasPartner)
        _players.value = GameSession.players.toList()
        _gameMessage.value = "Jugador agregado: $name"
        return true
    }

    fun removePlayer(playerId: Int) {
        GameSession.removePlayer(playerId)
        _players.value = GameSession.players.toList()
        _gameMessage.value = "Jugador eliminado"
    }

    fun toggleCategory(category: Category) {
        if (GameSession.selectedCategories.contains(category)) {
            GameSession.selectedCategories.remove(category)
        } else {
            GameSession.selectedCategories.add(category)
        }
        _selectedCategories.value = GameSession.selectedCategories.toSet()
    }

    fun setIntensity(level: Int) {
        GameSession.intensity = level.coerceIn(1, 3)
        _intensity.value = GameSession.intensity
    }

    fun canStartGame(): Boolean {
        return GameSession.players.size >= 2
    }

    fun canStartMatch(): Boolean {
        return GameSession.selectedCategories.isNotEmpty() && GameSession.players.size >= 2
    }

    fun startGame() {
        GameSession.resetGameOnly()
        updateCurrentPlayer()
        loadNewQuestion()
    }

    fun nextTurn() {
        GameSession.nextPlayer()
        updateCurrentPlayer()
        loadNewQuestion()
    }

    fun loadNewQuestion() {
        val current = GameSession.getCurrentPlayer()
        if (current == null) {
            _currentQuestion.value = null
            return
        }

        val availableQuestions = QuestionDatabase.getFilteredQuestions(
            selectedCategories = GameSession.selectedCategories,
            intensityLevel = GameSession.intensity,
            playerHasPartner = current.hasPartner
        ).filter { it.id !in GameSession.askedQuestionIds }

        val question = if (availableQuestions.isNotEmpty()) {
            availableQuestions.random()
        } else {
            // Si se acabaron, reiniciar y tomar una random
            GameSession.askedQuestionIds.clear()
            QuestionDatabase.getRandomQuestion(
                selectedCategories = GameSession.selectedCategories,
                intensityLevel = GameSession.intensity,
                playerHasPartner = current.hasPartner
            )
        }

        question?.let {
            GameSession.askedQuestionIds.add(it.id)
        }

        _currentQuestion.value = question
    }

    fun refreshAnotherQuestion() {
        loadNewQuestion()
    }

    private fun updateCurrentPlayer() {
        _currentPlayer.value = GameSession.getCurrentPlayer()
    }

    fun getIntensityLabel(): String {
        return when (GameSession.intensity) {
            1 -> "Suave 😊"
            2 -> "Medio 😏"
            3 -> "Extremo 🔥🔥🔥"
            else -> "Medio"
        }
    }

    fun resetSession() {
        GameSession.reset()
        _players.value = emptyList()
        _selectedCategories.value = emptySet()
        _intensity.value = 2
        _currentPlayer.value = null
        _currentQuestion.value = null
    }
}
