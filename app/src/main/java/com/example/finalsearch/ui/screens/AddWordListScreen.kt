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

// to create a new vocabulary list

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordListScreen(
    viewModel: WordListViewModel,
    onNavigateBack: () -> Unit
) {
    var listName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Word List") },
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
                text = "Create a new vocabulary list",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // listname input
            OutlinedTextField(
                value = listName,
                onValueChange = {
                    listName = it
                    showError = false
                },
                label = { Text("List Name") },
                placeholder = { Text("e.g., Dutch Verbs") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showError && listName.isBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // optionel description input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                placeholder = { Text("e.g., Common Dutch verbs for daily use") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3
            )

            if (showError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Please enter a list name",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // create button
            Button(
                onClick = {
                    if (listName.isBlank()) {
                        showError = true
                    } else {
                        viewModel.addWordList(listName.trim(), description.trim())
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create List", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
