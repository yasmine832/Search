package com.example.finalsearch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalsearch.viewmodel.GameViewModel

//game screen for word grid
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    listId: Int,
    onNavigateBack: () -> Unit
) {
    // Creates ViewModel with factory to pass listId
    val context = LocalContext.current
    val viewModel: GameViewModel = viewModel(
        factory = GameViewModelFactory(context.applicationContext as android.app.Application, listId)
    )

    val gameState by viewModel.gameState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Word Search") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetGame() }) {
                        Icon(Icons.Default.Refresh, "New Game")
                    }
                }
            )
        }
    ) { paddingValues ->

        if (gameState.grid == null) {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Game UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Score
                Text(
                    text = "Score: ${gameState.score}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Current clue/definition
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = if (gameState.isComplete) {
                            " Finished! Score: ${gameState.score}"
                        } else {
                            "Find: ${gameState.currentDefinition}"
                        },
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Grid
                WordSearchGridView(
                    grid = gameState.grid!!,
                    selectedCells = gameState.selectedCells,
                    foundWords = gameState.foundWords,
                    onCellClick = { row, col -> viewModel.onCellSelected(row, col) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.clearSelection() },
                        enabled = gameState.selectedCells.isNotEmpty()
                    ) {
                        Text("Clear")
                    }

                    Button(
                        onClick = { viewModel.checkSelection() },
                        enabled = gameState.selectedCells.isNotEmpty()
                    ) {
                        Text("Submit")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Found words as list
                Text(
                    text = "Found: ${gameState.foundWords.size}/${gameState.grid?.placedWords?.size ?: 0}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}

//Actual grid of letters
@Composable
fun WordSearchGridView(
    grid: com.example.finalsearch.game.WordSearchGrid,
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
            .size(32.dp)
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

//Check if cell is part of word
fun isPartOfFoundWord(
    row: Int,
    col: Int,
    placedWords: List<com.example.finalsearch.game.PlacedWord>,
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

//Factory to create GameViewModel with parameters

class GameViewModelFactory(
    private val application: android.app.Application,
    private val wordListId: Int
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(application, wordListId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}