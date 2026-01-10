package com.example.finalsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.finalsearch.data.SampleData
import com.example.finalsearch.data.VocabDatabase
import com.example.finalsearch.data.WordRepository
import com.example.finalsearch.ui.screens.AppNavigation
import com.example.finalsearch.ui.theme.FinalSearchTheme
import com.example.finalsearch.viewmodel.WordListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinalSearchTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FinalSearchApp()
                }
            }
        }
    }
}


@Composable
fun FinalSearchApp() {
    val navController = rememberNavController()
    val viewModel: WordListViewModel = viewModel()

    // Load sample data on first run
    LoadSampleDataOnce(viewModel)

    // Navigation
    AppNavigation(
        navController = navController,
        viewModel = viewModel
    )
}

/**
 * Load sample data once when app starts
 * Checks if database is empty, then adds Dutch-English words
 */
@Composable
fun LoadSampleDataOnce(viewModel: WordListViewModel) {
    // Get context OUTSIDE LaunchedEffect (in Composable scope)
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        // Now we can use context safely here
        val database = VocabDatabase.getDatabase(context)
        val repository = WordRepository(database.wordDao())

        // Check if database is empty
        var isFirstCheck = true
        repository.getAllWordLists().collect { lists ->
            if (lists.isEmpty() && isFirstCheck) {
                // Add sample Dutch-English vocabulary
                SampleData.populateDatabase(repository)
                isFirstCheck = false
            }
        }
    }
}