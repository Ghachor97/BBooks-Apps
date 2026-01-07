package com.galang.bbooks.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.galang.bbooks.ui.navigation.BottomNavItem
import com.galang.bbooks.ui.navigation.Screen
import com.galang.bbooks.ui.theme.DeepBlue
import com.galang.bbooks.ui.theme.PastelBlue
import com.galang.bbooks.ui.theme.SoftBlue
import com.galang.bbooks.ui.theme.SoftCoral

@Composable
fun MainScreen(
    onBookClick: (Long) -> Unit,
    onLogout: () -> Unit,
    onProfileClick: () -> Unit
) {
    val navController = rememberNavController()
    
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.BookList,
        BottomNavItem.Return,
        BottomNavItem.History,
    )

    Scaffold(
        containerColor = PastelBlue, // Match the theme directly to fix corners simply
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        // We remove the TopAppBar here to let each screen handle its own or use the new Dashboard Header
        bottomBar = {
             ModernBottomNavBar(
                 items = items,
                 navController = navController,
                 onProfileClick = onProfileClick
             )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Main.Dashboard.route,
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(), 
                bottom = innerPadding.calculateBottomPadding() 
            ) 
            // NOTE: If we want to strictly fix the corners being black, the easiest way 
            // without complex inset handling for every screen is setting containerColor above.
            // Using PastelBlue as containerColor covers the black window background.
        ) {
            composable(Screen.Main.Dashboard.route) {
                DashboardScreen()
            }
            composable(Screen.Main.BookList.route) {
                BookListScreen(onBookClick = onBookClick)
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

@Composable
fun ModernBottomNavBar(
    items: List<BottomNavItem>,
    navController: androidx.navigation.NavController,
    onProfileClick: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .height(60.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
                val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                
                ModernNavItem(
                    icon = screen.icon,
                    title = screen.title,
                    isSelected = isSelected,
                    onClick = {
                         navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            
            // Profile Item (Custom)
             ModernNavItem(
                icon = Icons.Default.AccountCircle, // Or use the profile icon
                title = "Profile",
                isSelected = false, // Always false as it navigates out
                onClick = onProfileClick
            )
        }
    }
}

@Composable
fun ModernNavItem(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) PastelBlue else Color.Transparent
    val contentColor = if (isSelected) SoftBlue else Color.Gray

    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            AnimatedVisibility(visible = isSelected) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
