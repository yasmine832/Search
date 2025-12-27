package com.example.finalsearch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalsearch.data.WordRepository
import com.example.finalsearch.model.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//for word search game

class GameViewModel(
    private val repository: WordRepository,
    private val wordListId: Int
) : ViewModel() {

    // Game state
    data class GameState(
        val words: List<Word> = emptyList(),
        val foundWords: Set<Int> = emptySet(),  // ids of found words
        val score: Int = 0,
        val isGameComplete: Boolean = false
    )

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    init {
        loadWords()
    }

    //Load words for practice using spaced repetion
    private fun loadWords() {
        viewModelScope.launch {
            repository.getWordsNeedingPractice(wordListId, limit = 10)
                .collect { words ->
                    _gameState.value = _gameState.value.copy(
                        words = words.shuffled()  //TODO random odrer
                    )
                }
        }
    }

    // Mark word as found in game and update as found in db
    fun markWordFound(word: Word, wasCorrect: Boolean) {
        viewModelScope.launch {
            // Update database
            repository.updateWordProgress(word, wasCorrect)

            // Update UI state
            _gameState.value = _gameState.value.copy(
                foundWords = _gameState.value.foundWords + word.id,
                score = if (wasCorrect) _gameState.value.score + 1 else _gameState.value.score,
                isGameComplete = _gameState.value.foundWords.size + 1 >= _gameState.value.words.size
            )
        }
    }

    //reset/PLAY AGAIN
    fun resetGame() {
        _gameState.value = GameState()
        loadWords()
    }
}