package com.example.finalsearch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalsearch.model.WordList
import com.example.finalsearch.viewmodel.WordListViewModel
import com.example.finalsearch.ui.components.EmptyState
import com.example.finalsearch.ui.components.WordListCard

//Home Screen has to show all word lists
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: WordListViewModel,
    onNavigateToWordList: (Int) -> Unit,
    onNavigateToAddList: () -> Unit
) {

    // controleer if UI update when db change .?
    val wordLists by viewModel.allWordLists.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "FinalSearch",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddList) {
                Icon(Icons.Default.Add, "Add word list")
            }
        }
    ) { paddingValues ->

        if (wordLists.isEmpty()) {
            // Empty state when none
            EmptyState(modifier = Modifier.padding(paddingValues))
        } else {
            // show all word lists
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(wordLists) { wordList ->
                    WordListCard(
                        wordList = wordList,
                        onClick = { onNavigateToWordList(wordList.id) }
                    )
                }
            }
        }
    }
}

