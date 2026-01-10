package com.example.finalsearch.game

import com.example.finalsearch.model.Word
import kotlin.random.Random

/**
 * Word Search Grid Generator
 *
 * SOURCE: Adapted from Microsoft Copilot conversation
 * https://copilot.microsoft.com/shares/TK1gfQt2hWBRd9LEqVEKo
 *
 * Generates a 10x10 letter grid with hidden vocabulary words
 */
class WordSearchGenerator {

    // Constants
    private val gridSize = 10
    private val maxWords = 7
    private val random = Random(System.currentTimeMillis())

    /**
     * Generate grid from list of words
     */
    fun generateGrid(words: List<Word>): WordSearchGrid {
        // Sort by difficulty (hardest first)
        val sortedWords = words
            .sortedByDescending { it.difficulty }
            .take(maxWords)

        // Create empty grid
        val grid = Array(gridSize) { CharArray(gridSize) { ' ' } }

        // Track placed words
        val placedWords = mutableListOf<PlacedWord>()

        // Try to place each word
        for (word in sortedWords) {
            val placed = tryPlaceWord(grid, word.wordText.uppercase())
            if (placed != null) {
                placedWords.add(placed)
            }
        }

        // Fill empty spaces
        fillEmpty(grid)

        return WordSearchGrid(grid, placedWords, sortedWords)
    }

    /**
     * Try to place word in grid (max 100 attempts)
     */
    private fun tryPlaceWord(grid: Array<CharArray>, word: String): PlacedWord? {
        repeat(100) {
            val direction = random.nextInt(3)  // 0=H, 1=V, 2=D
            val startRow = random.nextInt(gridSize)
            val startCol = random.nextInt(gridSize)

            if (canPlace(grid, word, startRow, startCol, direction)) {
                placeWord(grid, word, startRow, startCol, direction)

                // Calculate end position
                val (dRow, dCol) = getDirection(direction)
                val endRow = startRow + dRow * (word.length - 1)
                val endCol = startCol + dCol * (word.length - 1)

                return PlacedWord(word, startRow, startCol, endRow, endCol, direction)
            }
        }
        return null
    }

    /**
     * Check if word fits without collision
     */
    private fun canPlace(
        grid: Array<CharArray>,
        word: String,
        startRow: Int,
        startCol: Int,
        direction: Int
    ): Boolean {
        val (dRow, dCol) = getDirection(direction)
        val endRow = startRow + dRow * (word.length - 1)
        val endCol = startCol + dCol * (word.length - 1)

        // Check bounds
        if (endRow !in 0 until gridSize || endCol !in 0 until gridSize) {
            return false
        }

        // Check collisions
        for (i in word.indices) {
            val r = startRow + i * dRow
            val c = startCol + i * dCol
            val existing = grid[r][c]

            if (existing != ' ' && existing != word[i]) {
                return false
            }
        }

        return true
    }

    /**
     * Place word in grid
     */
    private fun placeWord(
        grid: Array<CharArray>,
        word: String,
        startRow: Int,
        startCol: Int,
        direction: Int
    ) {
        val (dRow, dCol) = getDirection(direction)

        for (i in word.indices) {
            val r = startRow + i * dRow
            val c = startCol + i * dCol
            grid[r][c] = word[i]
        }
    }

    /**
     * Get movement direction
     * 0 = Horizontal (0, 1)
     * 1 = Vertical (1, 0)
     * 2 = Diagonal (1, 1)
     */
    private fun getDirection(direction: Int): Pair<Int, Int> {
        return when (direction) {
            0 -> 0 to 1      // Horizontal
            1 -> 1 to 0      // Vertical
            else -> 1 to 1   // Diagonal
        }
    }

    /**
     * Fill empty spaces with random letters
     */
    private fun fillEmpty(grid: Array<CharArray>) {
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                if (grid[r][c] == ' ') {
                    grid[r][c] = ('A'..'Z').random()
                }
            }
        }
    }
}

/**
 * Result of grid generation
 */
data class WordSearchGrid(
    val grid: Array<CharArray>,
    val placedWords: List<PlacedWord>,
    val originalWords: List<Word>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WordSearchGrid) return false
        return grid.contentDeepEquals(other.grid) &&
                placedWords == other.placedWords &&
                originalWords == other.originalWords
    }

    override fun hashCode(): Int {
        var result = grid.contentDeepHashCode()
        result = 31 * result + placedWords.hashCode()
        result = 31 * result + originalWords.hashCode()
        return result
    }
}

/**
 * Information about placed word
 */
data class PlacedWord(
    val word: String,
    val startRow: Int,
    val startCol: Int,
    val endRow: Int,
    val endCol: Int,
    val direction: Int
)