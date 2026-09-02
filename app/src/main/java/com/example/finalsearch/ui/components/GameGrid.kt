package com.example.finalsearch.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalsearch.game.WordSearchGrid
import com.example.finalsearch.utils.isPartOfFoundWord

//Actual grid of letters
@Composable
fun WordSearchGridView(
    grid: WordSearchGrid,
    selectedCells: List<Pair<Int, Int>>,
    foundWords: Set<String>,
    onCellClick: (Int, Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        grid.grid.forEachIndexed { rowIndex, row ->
            Row {
                row.forEachIndexed { colIndex, letter ->
                    val isSelected = (rowIndex to colIndex) in selectedCells
                    val isInFoundWord = isPartOfFoundWord(
                        rowIndex, colIndex, grid.placedWords, foundWords
                    )

                    GridCell(
                        letter = letter,
                        isSelected = isSelected,
                        isFound = isInFoundWord,
                        onClick = { onCellClick(rowIndex, colIndex) }
                    )
                }
            }
        }
    }
}

//Aparte cel
@Composable
fun GridCell(
    letter: Char,
    isSelected: Boolean,
    isFound: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isFound -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        isSelected -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surface
    }

    Box(
        modifier = Modifier
            .size(30.dp)
            .padding(1.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}