package com.example.finalsearch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
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

