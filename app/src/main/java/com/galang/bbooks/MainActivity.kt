package com.galang.bbooks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
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
import com.galang.bbooks.ui.screens.RegisterScreen
import com.galang.bbooks.ui.theme.BBooksTheme
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as BBooksApplication
        
        // Simple blocking check for auto-login before UI renders
        var startDestination = Screen.Login.route
        runBlocking {
            if (app.userRepository.tryAutoLogin()) {
                startDestination = Screen.Main.route
            }
        }

        enableEdgeToEdge()
        setContent {
            BBooksTheme {
                val navController = rememberNavController()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController, 
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Login.route) {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate(Screen.Main.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onRegisterClick = {
                                    navController.navigate(Screen.Register.route)
                                }
                            )
                        }
                        
                        composable(Screen.Register.route) {
                             RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate(Screen.Main.route) {
                                         popUpTo(Screen.Register.route) { inclusive = true }
                                    }
                                },
                                onLoginClick = {
                                    navController.popBackStack()
                                }
                             )
                        }

                        composable(Screen.Main.route) {
                            MainScreen(
                                onBookClick = { bookId ->
                                    navController.navigate(Screen.BookDetail.createRoute(bookId))
                                },
                                onLogout = { 
                                     // This callback might be redundant if Profile handles it, 
                                     // but MainScreen needs to pass it possibly?
                                     // Let's check MainScreen implementation.
                                     // It seems MainScreen handles profile navigation internally or via top bar.
                                     // We will handle navigation mainly here.
                                },
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
                                        popUpTo(0) { inclusive = true }  // Clear entire stack
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
}