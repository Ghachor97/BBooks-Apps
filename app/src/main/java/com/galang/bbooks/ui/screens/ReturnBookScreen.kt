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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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

    val transactions by viewModel.activeTransactions.collectAsState()
    val borrowState by viewModel.returnState.collectAsState()
    val user = app.userRepository.currentUser
    
    var showConditionDialog by remember { mutableStateOf<TransactionWithBook?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Pengembalian Buku",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (transactions.isEmpty()) {
            Text("Tidak ada buku yang sedang dipinjam.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions) { item ->
                    ReturnItemCard(
                        item = item,
                        isAdmin = user?.role == "admin",
                        onReturnClick = {
                             if (user?.role == "admin") {
                                 showConditionDialog = item
                             } else {
                                 viewModel.returnBook(item) // Standard return for user
                             }
                        }
                    )
                }
            }
        }
    }

    if (showConditionDialog != null) {
        ReturnConditionDialog(
            item = showConditionDialog!!,
            onDismiss = { showConditionDialog = null },
            onConfirm = { condition ->
                viewModel.returnBook(showConditionDialog!!, condition)
                showConditionDialog = null
            }
        )
    }

    when (val state = borrowState) {
        is BorrowState.Error -> {
            AlertDialog(
                onDismissRequest = viewModel::dismissState,
                title = { Text("Error") },
                text = { Text(state.message) },
                confirmButton = {
                    TextButton(onClick = viewModel::dismissState) { Text("OK") }
                }
            )
        }
        is BorrowState.Success -> {
           AlertDialog(
                onDismissRequest = viewModel::dismissState,
                title = { Text("Berhasil") },
                text = { Text(state.message) },
                confirmButton = {
                    TextButton(onClick = viewModel::dismissState) { Text("OK") }
                }
            )
        }
        BorrowState.Loading -> {
             // Optional: Show loading indicator
        }
        BorrowState.Idle -> {}
    }
}

@Composable
fun ReturnItemCard(item: TransactionWithBook, isAdmin: Boolean, onReturnClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dueDateStr = dateFormat.format(Date(item.transaction.dueDate))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.book.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (item.borrowerName != null) {
                    Text("Peminjam: ${item.borrowerName}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.tertiary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Batas Waktu: $dueDateStr", style = MaterialTheme.typography.bodyMedium)
                if (item.fineEstimate > 0) {
                     Text("Estimasi Denda: Rp ${item.fineEstimate.toInt()}", color = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onReturnClick,
                colors = if (isAdmin) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) else ButtonDefaults.buttonColors()
            ) {
                Text(if (isAdmin) "Denda" else "Kembalikan")
            }
        }
    }
}

@Composable
fun ReturnConditionDialog(
    item: TransactionWithBook,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    // Map of Display Name to Condition Key
    val options = listOf(
        "Rusak (Denda Rp 100.000)" to "Rusak",
        "Hilang (Denda Rp 150.000)" to "Hilang",
        "Robek (Denda Rp 50.000)" to "Robek"
    )
    var selectedOption by remember { mutableStateOf(options[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Kondisi Denda") },
        text = {
            Column {
                Text("Pilih alasan denda untuk buku ini:")
                Spacer(modifier = Modifier.height(8.dp))
                options.forEach { (displayName, key) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                             .fillMaxWidth()
                             .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (key == selectedOption.second),
                            onClick = { selectedOption = displayName to key }
                        )
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedOption.second) }
            ) {
                Text("Konfirmasi Denda")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
