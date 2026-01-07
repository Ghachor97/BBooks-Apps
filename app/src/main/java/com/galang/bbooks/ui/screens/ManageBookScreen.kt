package com.galang.bbooks.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galang.bbooks.BBooksApplication
import com.galang.bbooks.data.Book
import com.galang.bbooks.ui.viewmodel.ManageBookViewModel
import com.galang.bbooks.ui.viewmodel.ManageBookViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Buku") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Book")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(books) { book ->
                ManageBookItem(
                    book = book,
                    onEdit = { showEditDialog = book },
                    onDelete = { viewModel.deleteBook(book) }
                )
            }
        }
    }

    if (showAddDialog) {
        BookDialog(
            title = "Tambah Buku",
            onDismiss = { showAddDialog = false },
            onConfirm = { title, author, category, stock ->
                viewModel.addBook(title, author, category, stock)
                showAddDialog = false
            }
        )
    }

    showEditDialog?.let { book ->
        BookDialog(
            title = "Edit Buku",
            initialTitle = book.title,
            initialAuthor = book.author,
            initialCategory = book.category,
            initialStock = book.stock,
            onDismiss = { showEditDialog = null },
            onConfirm = { title, author, category, stock ->
                viewModel.updateBook(book, title, author, category, stock)
                showEditDialog = null
            }
        )
    }
}

@Composable
fun ManageBookItem(book: Book, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(book.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(book.author, style = MaterialTheme.typography.bodyMedium)
                Text("Stok: ${book.stock}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun BookDialog(
    title: String,
    initialTitle: String = "",
    initialAuthor: String = "",
    initialCategory: String = "",
    initialStock: Int = 0,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Int) -> Unit
) {
    var titleText by remember { mutableStateOf(initialTitle) }
    var authorText by remember { mutableStateOf(initialAuthor) }
    var categoryText by remember { mutableStateOf(initialCategory) }
    var stockText by remember { mutableStateOf(initialStock.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(value = titleText, onValueChange = { titleText = it }, label = { Text("Judul") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = authorText, onValueChange = { authorText = it }, label = { Text("Penulis") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = categoryText, onValueChange = { categoryText = it }, label = { Text("Kategori") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = stockText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) stockText = it },
                    label = { Text("Stok") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(titleText, authorText, categoryText, stockText.toIntOrNull() ?: 0)
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
