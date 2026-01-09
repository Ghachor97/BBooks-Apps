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
import com.galang.bbooks.ui.screens.GoogleSignInScreen
import com.galang.bbooks.ui.screens.MainScreen
import com.galang.bbooks.ui.screens.ManageBookScreen
import com.galang.bbooks.ui.screens.ProfileScreen
import com.galang.bbooks.ui.theme.BBooksTheme
import kotlinx.coroutines.runBlocking

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galang.bbooks.ui.viewmodel.ThemeViewModel
import com.galang.bbooks.ui.viewmodel.ThemeViewModelFactory

class MainActivity : ComponentActivity() {
    
    companion object {
        // Web Client ID from google-services.json (client_type 3)
        const val WEB_CLIENT_ID = "137620824606-0r2tno4jet8qm7ntgc2pkkm1utfmd098.apps.googleusercontent.com"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as BBooksApplication
        
        // Simple blocking check for auto-login before UI renders
        var startDestination = Screen.GoogleSignIn.route
        runBlocking {
            if (app.userRepository.tryAutoLogin()) {
                startDestination = Screen.Main.route
            }
        }

        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModelFactory(app.container.userPreferences)
            )
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            BBooksTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController, 
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.GoogleSignIn.route) {
                            GoogleSignInScreen(
                                onSignInSuccess = {
                                    navController.navigate(Screen.Main.route) {
                                        popUpTo(Screen.GoogleSignIn.route) { inclusive = true }
                                    }
                                },
                                webClientId = WEB_CLIENT_ID
                            )
                        }

                        composable(Screen.Main.route) {
                            MainScreen(
                                onBookClick = { bookId ->
                                    navController.navigate(Screen.BookDetail.createRoute(bookId))
                                },
                                onLogout = { },
                                onProfileClick = {
                                    navController.navigate(Screen.Profile.route)
                                },
                                onAddBook = {
                                    navController.navigate(Screen.ManageBooks.route)
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
                                    navController.navigate(Screen.GoogleSignIn.route) {
                                        popUpTo(0) { inclusive = true }  // Clear entire stack
                                    }
                                },
                                onManageBooks = {
                                    navController.navigate(Screen.ManageBooks.route)
                                },
                                isDarkTheme = isDarkTheme,
                                onThemeChange = { isDark ->
                                    themeViewModel.toggleTheme(isDark)
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