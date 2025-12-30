package com.example.finalsearch.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.finalsearch.viewmodel.WordListViewModel
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddWordList : Screen("add_word_list")
    object WordListDetail : Screen("word_list_detail/{listId}") {
        fun createRoute(listId: Int) = "word_list_detail/$listId"
    }
    object AddWord : Screen("add_word/{listId}") {
        fun createRoute(listId: Int) = "add_word/$listId"
    }
    object Game : Screen("game/{listId}") {
        fun createRoute(listId: Int) = "game/$listId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: WordListViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home is the list of all wordlists
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToWordList = { listId ->
                    navController.navigate(Screen.WordListDetail.createRoute(listId))
                },
                onNavigateToAddList = {
                    navController.navigate(Screen.AddWordList.route)  // ← FIX!
                }
            )
        }

        // shows word list details
        composable(
            route = Screen.WordListDetail.route,
            arguments = listOf(
                navArgument("listId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getInt("listId") ?: return@composable

            WordListDetailScreen(
                listId = listId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddWord = {
                    navController.navigate(Screen.AddWord.createRoute(listId))
                },
                onNavigateToGame = {
                    navController.navigate(Screen.Game.createRoute(listId))
                }
            )
        }

        composable(
            route = Screen.AddWord.route,
            arguments = listOf(
                navArgument("listId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getInt("listId") ?: return@composable

            AddWordScreen(
                listId = listId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // add word scree
        composable(Screen.AddWordList.route) {
            AddWordListScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // game screen todo
        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("listId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getInt("listId") ?: return@composable

            GameScreen(
                listId = listId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}