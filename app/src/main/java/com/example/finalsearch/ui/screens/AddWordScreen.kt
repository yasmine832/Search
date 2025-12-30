package com.example.finalsearch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalsearch.viewmodel.WordListViewModel

//(manually for now)form to add new words
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordScreen(
    listId: Int,
    viewModel: WordListViewModel,
    onNavigateBack: () -> Unit
) {
    var wordText by remember { mutableStateOf("") }
    var definition by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Word") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "English ↔ Dutch",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // dutch word input
            OutlinedTextField(
                value = wordText,
                onValueChange = {
                    wordText = it
                    showError = false
                },
                label = { Text("Dutch Word") },
                placeholder = { Text("e.g., hond") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showError && wordText.isBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // trans
            OutlinedTextField(
                value = definition,
                onValueChange = {
                    definition = it
                    showError = false
                },
                label = { Text("English Definition") },
                placeholder = { Text("e.g., dog") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showError && definition.isBlank()
            )

            if (showError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Please fill in both fields",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // voor the add button
            Button(
                onClick = {
                    if (wordText.isBlank() || definition.isBlank()) {
                        showError = true
                    } else {
                        viewModel.addWord(listId, wordText.trim(), definition.trim())
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Word", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}