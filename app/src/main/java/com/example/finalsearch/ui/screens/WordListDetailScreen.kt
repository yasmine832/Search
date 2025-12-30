package com.example.finalsearch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalsearch.model.Word
import com.example.finalsearch.viewmodel.WordListViewModel

//SHow all words incl progrress
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordListDetailScreen(
    listId: Int,
    viewModel: WordListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddWord: () -> Unit,
    onNavigateToGame: () -> Unit
) {
    //get words for this list
    val words by viewModel.getWordsForList(listId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Words") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Play button
                    IconButton(
                        onClick = onNavigateToGame,
                        enabled = words.isNotEmpty()
                    ) {
                        Icon(Icons.Default.PlayArrow, "Practice")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddWord) {
                Icon(Icons.Default.Add, "Add word")
            }
        }
    ) { paddingValues ->

        if (words.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No words yet",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tap + to add your first word")
            }
        } else {
            //show words with progress
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(words) { word ->
                    WordProgressCard(word = word)
                }
            }
        }
    }
}

//Card showing word with progress indicators

@Composable
fun WordProgressCard(word: Word) {
    // Color based on difficulty (red=struggling, green=mastered)
    val cardColor = when (word.difficulty) {
        0, 1 -> MaterialTheme.colorScheme.errorContainer
        2, 3 -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Word and definition
            Text(
                text = word.wordText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = word.definition,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progress indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Difficulty level (0-5 stars)
                Text(
                    text = "Level: ${"★".repeat(word.difficulty)}${"☆".repeat(5 - word.difficulty)}",
                    fontSize = 14.sp
                )

                // Success rate
                if (word.timesShown > 0) {
                    Text(
                        text = "${word.successRate.toInt()}% success",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "Not practiced",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            // Progress bar
            if (word.timesShown > 0) {
                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = word.successRate / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}