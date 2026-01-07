package com.galang.bbooks.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galang.bbooks.BBooksApplication
import com.galang.bbooks.ui.viewmodel.BorrowState
import com.galang.bbooks.ui.viewmodel.ReturnBookViewModel
import com.galang.bbooks.ui.viewmodel.ReturnBookViewModelFactory
import com.galang.bbooks.ui.viewmodel.TransactionWithBook
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReturnBookScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as BBooksApplication
    val viewModel: ReturnBookViewModel = viewModel(
        factory = ReturnBookViewModelFactory(
            app.transactionRepository,
            app.bookRepository,
            app.userRepository
        )
    )

    val activeTransactions by viewModel.activeTransactions.collectAsState()
    val returnState by viewModel.returnState.collectAsState()

    var showConfirmDialog by remember { mutableStateOf<TransactionWithBook?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Pengembalian Buku",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (activeTransactions.isEmpty()) {
            Text("Tidak ada buku yang sedang dipinjam.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(activeTransactions) { item ->
                    ReturnItemCard(
                        item = item,
                        onReturnClick = { showConfirmDialog = item }
                    )
                }
            }
        }
    }
    
    // Status Logic
    if (returnState is BorrowState.Loading) {
         // Maybe show loading overlay or just button state
    }
    
    if (returnState is BorrowState.Success) {
        AlertDialog(
            onDismissRequest = viewModel::dismissState,
            title = { Text("Berhasil") },
            text = { Text((returnState as BorrowState.Success).message) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissState) { Text("OK") }
            }
        )
    }
    
     if (returnState is BorrowState.Error) {
        AlertDialog(
            onDismissRequest = viewModel::dismissState,
            title = { Text("Error") },
            text = { Text((returnState as BorrowState.Error).message) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissState) { Text("OK") }
            }
        )
    }

    showConfirmDialog?.let { item ->
        AlertDialog(
            onDismissRequest = { showConfirmDialog = null },
            title = { Text("Konfirmasi Pengembalian") },
            text = {
                Column {
                    Text("Kembalikan buku \"${item.book.title}\"?")
                    if (item.fineEstimate > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Estimasi Denda: Rp ${item.fineEstimate.toInt()}",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.returnBook(item)
                    showConfirmDialog = null
                }) {
                    Text("Kembalikan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun ReturnItemCard(item: TransactionWithBook, onReturnClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dueDateStr = dateFormat.format(Date(item.transaction.dueDate))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.book.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Batas Waktu: $dueDateStr", style = MaterialTheme.typography.bodyMedium)
            if (item.fineEstimate > 0) {
                 Text("Denda: Rp ${item.fineEstimate.toInt()}", color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onReturnClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Kembalikan")
            }
        }
    }
}
