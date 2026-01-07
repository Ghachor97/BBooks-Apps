package com.galang.bbooks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.galang.bbooks.ui.navigation.Screen
import com.galang.bbooks.ui.screens.BookDetailScreen
import com.galang.bbooks.ui.screens.LoginScreen
import com.galang.bbooks.ui.screens.MainScreen
import com.galang.bbooks.ui.screens.ManageBookScreen
import com.galang.bbooks.ui.screens.ProfileScreen
import com.galang.bbooks.ui.theme.BBooksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BBooksTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Screen.Login.route) {
                    composable(Screen.Login.route) {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.Main.route) {
                        MainScreen(
                            onBookClick = { bookId ->
                                navController.navigate(Screen.BookDetail.createRoute(bookId))
                            },
                            onLogout = { /* Handled in Profile */ },
                            onProfileClick = {
                                navController.navigate(Screen.Profile.route)
                            }
                        )
                    }
                    composable(
                        Screen.BookDetail.route,
                        arguments = listOf(navArgument("bookId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val bookId = backStackEntry.arguments?.getLong("bookId") ?: 0L
                        BookDetailScreen(
                            bookId = bookId,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.Profile.route) {
                        ProfileScreen(
                            onBack = { navController.popBackStack() },
                            onLogout = {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            },
                            onManageBooks = {
                                navController.navigate(Screen.ManageBooks.route)
                            }
                        )
                    }
                    composable(Screen.ManageBooks.route) {
                        ManageBookScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}