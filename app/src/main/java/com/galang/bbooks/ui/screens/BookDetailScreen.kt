package com.galang.bbooks.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galang.bbooks.BBooksApplication
import com.galang.bbooks.ui.viewmodel.BookDetailViewModel
import com.galang.bbooks.ui.viewmodel.BookDetailViewModelFactory
import com.galang.bbooks.ui.viewmodel.BorrowState

@Composable
fun BookDetailScreen(bookId: Long, onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as BBooksApplication
    val viewModel: BookDetailViewModel = viewModel(
        factory = BookDetailViewModelFactory(
            app.bookRepository,
            app.transactionRepository,
            app.userRepository,
            bookId
        )
    )

    val book by viewModel.book.collectAsState()
    val borrowState by viewModel.borrowState.collectAsState()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            book?.let { b ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = b.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "by ${b.author}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Info Row or details
                    Text("Kategori: ${b.category}")
                    Text("Stok: ${b.stock}")
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    Button(
                        onClick = viewModel::borrowBook,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = b.stock > 0 && b.isAvailable && borrowState !is BorrowState.Loading
                    ) {
                        Text(if (b.stock > 0) "Pinjam Buku" else "Stok Habis")
                    }
                }
            }
        }
    }

    if (borrowState is BorrowState.Success) {
        AlertDialog(
            onDismissRequest = viewModel::dismissState,
            title = { Text("Berhasil") },
            text = { Text((borrowState as BorrowState.Success).message) },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.dismissState() 
                    onBack() // Go back after success
                }) {
                    Text("OK")
                }
            }
        )
    }

    if (borrowState is BorrowState.Error) {
        AlertDialog(
            onDismissRequest = viewModel::dismissState,
            title = { Text("Gagal") },
            text = { Text((borrowState as BorrowState.Error).message) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissState) {
                    Text("OK")
                }
            }
        )
    }
}
