package com.galang.bbooks.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galang.bbooks.BBooksApplication
import com.galang.bbooks.data.Book
import com.galang.bbooks.ui.components.LiquidGlassCard
import com.galang.bbooks.ui.components.StatusBadge
import com.galang.bbooks.ui.theme.*
import com.galang.bbooks.ui.viewmodel.ManageBookViewModel
import com.galang.bbooks.ui.viewmodel.ManageBookViewModelFactory
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.material.icons.filled.Link
import androidx.compose.ui.layout.ContentScale

@Composable
fun ManageBookScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as BBooksApplication
    val viewModel: ManageBookViewModel = viewModel(
        factory = ManageBookViewModelFactory(app.bookRepository)
    )

    val books by viewModel.books.collectAsState()
    var showEditDialog by remember { mutableStateOf<Book?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredBooks = books.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
        it.author.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = DarkBackground,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PurpleAccent,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Book")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
        ) {
            // Decorative circles
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .offset(x = (-80).dp, y = (-50).dp)
                    .clip(CircleShape)
                    .background(PurpleAccent.copy(alpha = 0.06f))
            )
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .offset(x = 200.dp, y = 400.dp)
                    .clip(CircleShape)
                    .background(BlueAccent.copy(alpha = 0.05f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(GlassWhite)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Kelola Buku",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari buku...", color = TextTertiary) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = TextTertiary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleAccent,
                        unfocusedBorderColor = GlassBorder,
                        focusedContainerColor = GlassWhite,
                        unfocusedContainerColor = GlassWhite,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = PurpleAccent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Book List
                if (filteredBooks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Book,
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
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(filteredBooks) { book ->
                            ManageBookItem(
                                book = book,
                                onEdit = { showEditDialog = book },
                                onDelete = { viewModel.deleteBook(book) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Dialog
    if (showAddDialog) {
        BookFormDialog(
            title = "Tambah Buku",
            onDismiss = { showAddDialog = false },
            onConfirm = { title, author, category, stock, coverUrl ->
                viewModel.addBook(title, author, category, stock, coverUrl)
                showAddDialog = false
            }
        )
    }

    // Edit Dialog
    showEditDialog?.let { book ->
        BookFormDialog(
            title = "Edit Buku",
            initialTitle = book.title,
            initialAuthor = book.author,
            initialCategory = book.category,
            initialStock = book.stock,
            initialCoverUrl = book.coverUrl,
            onDismiss = { showEditDialog = null },
            onConfirm = { title, author, category, stock, coverUrl ->
                viewModel.updateBook(book, title, author, category, stock, coverUrl)
                showEditDialog = null
            }
        )
    }
}

@Composable
private fun ManageBookItem(
    book: Book,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book Cover
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PurpleAccent.copy(alpha = 0.3f), BlueAccent.copy(alpha = 0.2f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (book.coverUrl.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(book.coverUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Cover",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = PurpleAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Book Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusBadge(
                    text = "Stok: ${book.stock}",
                    color = if (book.stock > 0) StatusGreen else StatusRed
                )
            }

            // Action Buttons
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = PurpleAccent
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = StatusRed
                )
            }
        }
    }
}

@Composable
private fun BookFormDialog(
    title: String,
    initialTitle: String = "",
    initialAuthor: String = "",
    initialCategory: String = "",
    initialStock: Int = 0,
    initialCoverUrl: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Int, String) -> Unit
) {
    var titleText by remember { mutableStateOf(initialTitle) }
    var authorText by remember { mutableStateOf(initialAuthor) }
    var categoryText by remember { mutableStateOf(initialCategory) }
    var stockText by remember { mutableStateOf(initialStock.toString()) }
    var coverUrlText by remember { mutableStateOf(initialCoverUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = "Judul",
                    icon = Icons.Default.Book
                )
                FormField(
                    value = authorText,
                    onValueChange = { authorText = it },
                    label = "Penulis",
                    icon = Icons.Default.Person
                )
                FormField(
                    value = categoryText,
                    onValueChange = { categoryText = it },
                    label = "Kategori",
                    icon = Icons.Default.Category
                )
                FormField(
                    value = stockText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) stockText = it },
                    label = "Stok",
                    icon = Icons.Default.Inventory,
                    keyboardType = KeyboardType.Number
                )
                FormField(
                    value = coverUrlText,
                    onValueChange = { coverUrlText = it },
                    label = "Link Cover (Google Drive)",
                    icon = Icons.Default.Link
                )
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(PurpleAccent, BlueAccent)
                        )
                    )
                    .clickable {
                        onConfirm(titleText, authorText, categoryText, stockText.toIntOrNull() ?: 0, coverUrlText)
                    }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Simpan",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextTertiary) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PurpleAccent
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PurpleAccent,
            unfocusedBorderColor = GlassBorder,
            focusedContainerColor = GlassWhite,
            unfocusedContainerColor = GlassWhite,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = PurpleAccent,
            focusedLabelColor = PurpleAccent,
            unfocusedLabelColor = TextTertiary
        )
    )
}
