package com.example.finalsearch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalsearch.data.VocabDatabase
import com.example.finalsearch.data.WordRepository
import com.example.finalsearch.game.WordSearchGenerator
import com.example.finalsearch.game.WordSearchGrid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    application: Application,
    private val wordListId: Int
) : AndroidViewModel(application) {

    private val repository: WordRepository
    private val generator = WordSearchGenerator()

    // Game state
    data class GameState(
        val grid: WordSearchGrid? = null,
        val selectedCells: List<Pair<Int, Int>> = emptyList(),
        val foundWords: Set<String> = emptySet(),
        val currentTargetWord: String = "",      // NEW is which word user should find
        val currentDefinition: String = "",       // The clue
        val score: Int = 0,
        val isComplete: Boolean = false
    )

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    init {
        val database = VocabDatabase.getDatabase(application)
        repository = WordRepository(database.wordDao())
        loadWordsAndGenerateGrid()
    }


    ///Load words and generate grid

    private fun loadWordsAndGenerateGrid() {
        viewModelScope.launch {
            repository.getWordsForList(wordListId)
                .collect { words ->
                    if (words.isNotEmpty() && _gameState.value.grid == null) {
                        val grid = generator.generateGrid(words)

                        // Get first word to find
                        val firstPlaced = grid.placedWords.firstOrNull()
                        val firstWord = firstPlaced?.let { placed ->
                            grid.originalWords.find {
                                it.wordText.uppercase() == placed.word
                            }
                        }

                        _gameState.value = _gameState.value.copy(
                            grid = grid,
                            currentTargetWord = firstPlaced?.word ?: "",
                            currentDefinition = firstWord?.definition ?: ""
                        )
                    }
                }
        }
    }

    //When user selcts a cel
    fun onCellSelected(row: Int, col: Int) {
        val current = _gameState.value.selectedCells
        val cell = row to col

        //When empty just add first cell
        if (current.isEmpty()) {
            _gameState.value = _gameState.value.copy(
                selectedCells = listOf(cell)
            )
            return
        }

        //If click again, remove it
        if (cell == current.last()) {
            _gameState.value = _gameState.value.copy(
                selectedCells = current.dropLast(1)
            )
            return
        }

        // Check if new cell extends the line
        val newSelection = current + cell

        if (isValidLine(newSelection)) {
            // IF Valid line, we add the cell
            _gameState.value = _gameState.value.copy(
                selectedCells = newSelection
            )
        } else {
            //IF Invalid line we start new selection from this cell
            _gameState.value = _gameState.value.copy(
                selectedCells = listOf(cell)
            )
        }
    }

    /**
     * todo
     * Check if selected cells form a valid line
     * RULES:
     * Horizontaal: same row, columns consecutive
     * Verticaal: same column, rows consecutive
     * Diagonaal: rows and columns both increase by 1
     */
    private fun isValidLine(cells: List<Pair<Int, Int>>): Boolean {
        if (cells.size <= 1) return true

        val rows = cells.map { it.first }
        val cols = cells.map { it.second }

        val rowDiffs = rows.zipWithNext { a, b -> b - a }
        val colDiffs = cols.zipWithNext { a, b -> b - a }

        // Check horizontal (row constant, col increases by 1)
        val isHorizontal = rowDiffs.all { it == 0 } && colDiffs.all { it == 1 }

        // Check vertical (col constant, row increases by 1)
        val isVertical = colDiffs.all { it == 0 } && rowDiffs.all { it == 1 }

        // Check diagonal (both increase by 1)
        val isDiagonal = rowDiffs.all { it == 1 } && colDiffs.all { it == 1 }

        return isHorizontal || isVertical || isDiagonal
    }
//Check if selection matches target word
    fun checkSelection() {
        val grid = _gameState.value.grid ?: return
        val selected = _gameState.value.selectedCells
        val targetWord = _gameState.value.currentTargetWord

        if (selected.isEmpty() || targetWord.isEmpty()) return

        // Build word from selected cells
        val selectedWord = buildString {
            selected.forEach { (row, col) ->
                append(grid.grid[row][col])
            }
        }

        // Match current target word?
        if (selectedWord == targetWord) {
            // juist
            handleCorrectWord()
        } else {
            // Fout, clear selection
            _gameState.value = _gameState.value.copy(
                selectedCells = emptyList()
            )
        }
    }

    // skip to next word
    fun nextWord() {
        val grid = _gameState.value.grid ?: return
        val foundWords = _gameState.value.foundWords

        // get words not found yet
        val remaining = grid.placedWords.filter { it.word !in foundWords }
        if (remaining.isEmpty()) return

        val currentIndex = remaining.indexOfFirst { it.word == _gameState.value.currentTargetWord }
        val nextIndex = (currentIndex + 1) % remaining.size

        val nextPlaced = remaining[nextIndex]
        val nextWord = grid.originalWords.find { it.wordText.uppercase() == nextPlaced.word }

        _gameState.value = _gameState.value.copy(
            currentTargetWord = nextPlaced.word,
            currentDefinition = nextWord?.definition ?: "",
            selectedCells = emptyList()
        )
    }

    // go back  previous word
    fun previousWord() {
        val grid = _gameState.value.grid ?: return
        val foundWords = _gameState.value.foundWords

        val remaining = grid.placedWords.filter { it.word !in foundWords }
        if (remaining.isEmpty()) return

        val currentIndex = remaining.indexOfFirst { it.word == _gameState.value.currentTargetWord }
        val prevIndex = if (currentIndex <= 0) remaining.size - 1 else currentIndex - 1

        val prevPlaced = remaining[prevIndex]
        val prevWord = grid.originalWords.find { it.wordText.uppercase() == prevPlaced.word }

        _gameState.value = _gameState.value.copy(
            currentTargetWord = prevPlaced.word,
            currentDefinition = prevWord?.definition ?: "",
            selectedCells = emptyList()
        )
    }





    //Handle correct word found

    private fun handleCorrectWord() {
        val grid = _gameState.value.grid ?: return
        val targetWord = _gameState.value.currentTargetWord

        // Find original Word object for database update
        val originalWord = grid.originalWords.find {
            it.wordText.uppercase() == targetWord
        }

        // Update progress in database
        if (originalWord != null) {
            viewModelScope.launch {
                repository.updateWordProgress(originalWord, wasCorrect = true)
            }
        }

        // Update game state
        val newFoundWords = _gameState.value.foundWords + targetWord
        val newScore = _gameState.value.score + 10
        val isComplete = newFoundWords.size >= grid.placedWords.size

        // Get next word to find
        val nextPlaced = grid.placedWords.find { it.word !in newFoundWords }
        val nextWord = nextPlaced?.let { placed ->
            grid.originalWords.find {
                it.wordText.uppercase() == placed.word
            }
        }

        _gameState.value = _gameState.value.copy(
            foundWords = newFoundWords,
            score = newScore,
            selectedCells = emptyList(),
            currentTargetWord = nextPlaced?.word ?: "",
            currentDefinition = if (isComplete) {
                "Game Complete! 🎉"
            } else {
                nextWord?.definition ?: ""
            },
            isComplete = isComplete
        )
    }

    //Clear current selectio
    fun clearSelection() {
        _gameState.value = _gameState.value.copy(selectedCells = emptyList())
    }

    //Reset game
    fun resetGame() {
        _gameState.value = GameState()
        loadWordsAndGenerateGrid()
    }
}