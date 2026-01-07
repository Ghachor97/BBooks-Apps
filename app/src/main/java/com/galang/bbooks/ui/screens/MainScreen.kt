package com.galang.bbooks.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.galang.bbooks.ui.components.LiquidBottomNavBar
import com.galang.bbooks.ui.components.LiquidProfileNavItem
import com.galang.bbooks.ui.navigation.BottomNavItem
import com.galang.bbooks.ui.navigation.Screen
import com.galang.bbooks.ui.theme.DarkBackground

@Composable
fun MainScreen(
    onBookClick: (Long) -> Unit,
    onLogout: () -> Unit,
    onProfileClick: () -> Unit,
    onAddBook: () -> Unit
) {
    val navController = rememberNavController()
    
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.BookList,
        BottomNavItem.Return,
        BottomNavItem.History,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    Scaffold(
        containerColor = DarkBackground,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        bottomBar = {
            LiquidBottomNavBar(
                items = items,
                selectedRoute = currentRoute,
                onItemSelected = { item ->
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                extraItem = {
                    LiquidProfileNavItem(
                        icon = Icons.Default.AccountCircle,
                        title = "Profile",
                        onClick = onProfileClick
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Main.Dashboard.route,
                modifier = Modifier.padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
            ) {
                composable(Screen.Main.Dashboard.route) {
                    DashboardScreen()
                }
                composable(Screen.Main.BookList.route) {
                    BookListScreen(onBookClick = onBookClick, onAddBook = onAddBook)
                }
                composable(Screen.Main.ReturnBook.route) {
                    ReturnBookScreen()
                }
                composable(Screen.Main.History.route) {
                    HistoryScreen()
                }
            }
        }
    }
}
