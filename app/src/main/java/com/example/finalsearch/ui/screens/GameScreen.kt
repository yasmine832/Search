package com.example.finalsearch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalsearch.viewmodel.GameViewModel
import com.example.finalsearch.ui.components.WordSearchGridView

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

    var showDifficultyDialog by remember { mutableStateOf(true) }

    if (showDifficultyDialog && gameState.grid == null) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Kies moeilijkheidsgraad") },
            text = {
                Column {
                    Text("Easy: 3 woorden")
                    Text("Medium: 5 woorden")
                    Text("Hard: 7 woorden")
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = {
                        viewModel.setDifficulty("easy")
                        showDifficultyDialog = false
                    }) {
                        Text("Easy")
                    }
                    TextButton(onClick = {
                        viewModel.setDifficulty("medium")
                        showDifficultyDialog = false
                    }) {
                        Text("Medium")
                    }
                    TextButton(onClick = {
                        viewModel.setDifficulty("hard")
                        showDifficultyDialog = false
                    }) {
                        Text("Hard")
                    }
                }
            }
        )
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Word Search") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
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

        if (gameState.isComplete) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Klaar!") },
                text = {
                    Column {
                        Text("Score: ${gameState.score} punten")
                        Text("Woorden: ${gameState.foundWords.size}")
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.resetGame() }) {
                        Text("Opnieuw")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Terug")
                    }
                }
            )
        }

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

                // Current clue/definition  +SKIP
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Zoek het woord:",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.previousWord() },
                                enabled = !gameState.isComplete
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Previous")
                            }

                            Text(
                                text = if (gameState.isComplete) {
                                    "Klaar!"
                                } else {
                                    gameState.currentDefinition
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = { viewModel.nextWord() },
                                enabled = !gameState.isComplete
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next")
                            }
                        }
                    }
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