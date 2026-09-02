package com.example.finalsearch.utils

import com.example.finalsearch.game.PlacedWord

//Check if cell is part of word
fun isPartOfFoundWord(
    row: Int,
    col: Int,
    placedWords: List<PlacedWord>,
    foundWords: Set<String>
): Boolean {
    return placedWords.any { placed ->
        if (placed.word !in foundWords) return@any false

        val (dRow, dCol) = when (placed.direction) {
            0 -> 0 to 1
            1 -> 1 to 0
            else -> 1 to 1
        }

        for (i in placed.word.indices) {
            val r = placed.startRow + i * dRow
            val c = placed.startCol + i * dCol
            if (r == row && c == col) return true
        }
        false
    }
}