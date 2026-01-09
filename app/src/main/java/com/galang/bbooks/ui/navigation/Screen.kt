package com.galang.bbooks.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object GoogleSignIn : Screen("google_sign_in")
    data object Main : Screen("main") {
        data object Dashboard : Screen("dashboard")
        data object BookList : Screen("book_list")
        data object ReturnBook : Screen("return_book")
        data object History : Screen("history")
    }
    data object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: Long) = "book_detail/$bookId"
    }
    data object ManageBooks : Screen("manage_books")
    data object Profile : Screen("profile")
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Dashboard : BottomNavItem(Screen.Main.Dashboard.route, "Beranda", Icons.Default.Dashboard)
    data object BookList : BottomNavItem(Screen.Main.BookList.route, "Buku", Icons.Default.Book)
    data object Return : BottomNavItem(Screen.Main.ReturnBook.route, "Pengembalian", Icons.Default.CheckCircle)
    data object History : BottomNavItem(Screen.Main.History.route, "Riwayat", Icons.Default.History)
}
