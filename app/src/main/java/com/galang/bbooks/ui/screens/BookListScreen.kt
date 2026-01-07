package com.galang.bbooks.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galang.bbooks.BBooksApplication
import com.galang.bbooks.data.Book
import com.galang.bbooks.ui.components.LiquidGlassBookCard
import com.galang.bbooks.ui.components.LiquidGlassCard
import com.galang.bbooks.ui.components.StatusBadge
import com.galang.bbooks.ui.theme.*
import com.galang.bbooks.ui.viewmodel.BookListViewModel
import com.galang.bbooks.ui.viewmodel.BookListViewModelFactory

@Composable
fun BookListScreen(onBookClick: (Long) -> Unit, onAddBook: () -> Unit = {}) {
    val context = LocalContext.current
    val app = context.applicationContext as BBooksApplication
    val viewModel: BookListViewModel = viewModel(
        factory = BookListViewModelFactory(app.bookRepository)
    )
    val user = app.userRepository.currentUser
    val isAdmin = user?.role == "admin"

    val books by viewModel.books.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    var selectedFilter by remember { mutableStateOf("Semua") }
    val filters = listOf("Semua", "Tersedia", "Dipinjam")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Decorative background
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = (-80).dp, y = (-50).dp)
                .clip(CircleShape)
                .background(PurpleAccent.copy(alpha = 0.06f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Header
            Text(
                text = "Daftar Buku",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Search Bar with Glass Effect
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { 
                    Text(
                        "Cari judul atau penulis...",
                        color = TextTertiary
                    ) 
                },
                leadingIcon = { 
                    Icon(
                        Icons.Default.Search, 
                        contentDescription = null, 
                        tint = PurpleAccent
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlassWhite),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = PurpleAccent,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = PurpleAccent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        text = filter,
                        isSelected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Book List
            val filteredBooks = when (selectedFilter) {
                "Tersedia" -> books.filter { it.stock > 0 }
                "Dipinjam" -> books.filter { it.stock <= 0 }
                else -> books
            }

            if (filteredBooks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tidak ada buku ditemukan",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(filteredBooks) { book ->
                        BookItemCard(
                            book = book,
                            onClick = { onBookClick(book.id) }
                        )
                    }
                }
            }
        }

        // Floating Action Button for Admin
        if (isAdmin) {
            FloatingActionButton(
                onClick = onAddBook,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 24.dp),
                containerColor = PurpleAccent,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Buku")
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(listOf(PurpleAccent, BlueAccent))
                } else {
                    Brush.horizontalGradient(listOf(GlassWhite, GlassWhite))
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else TextSecondary
        )
    }
}

@Composable
private fun BookItemCard(
    book: Book,
    onClick: () -> Unit
) {
    val isAvailable = book.stock > 0
    
    LiquidGlassBookCard(
        title = book.title,
        author = "karya ${book.author}",
        onClick = onClick,
        coverContent = {
            // Book cover with gradient
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = if (isAvailable) {
                                listOf(PurpleAccent, BlueAccent)
                            } else {
                                listOf(DarkSurfaceVariant, DarkSurface)
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = if (isAvailable) Color.White.copy(alpha = 0.7f) else TextTertiary,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        additionalInfo = {
            // Rating row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = StatusOrange,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "4.5",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Stok: ${book.stock}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
        },
        statusBadge = {
            StatusBadge(
                text = if (isAvailable) "Tersedia" else "Dipinjam",
                color = if (isAvailable) StatusGreen else StatusRed
            )
        }
    )
}
