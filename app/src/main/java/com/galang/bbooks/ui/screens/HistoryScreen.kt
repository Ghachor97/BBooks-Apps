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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.galang.bbooks.BBooksApplication
import com.galang.bbooks.ui.viewmodel.HistoryViewModel
import com.galang.bbooks.ui.viewmodel.HistoryViewModelFactory
import com.galang.bbooks.ui.viewmodel.TransactionWithBook
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as BBooksApplication
    val viewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModelFactory(
            app.transactionRepository,
            app.bookRepository,
            app.userRepository
        )
    )

    val history by viewModel.history.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Riwayat Aktivitas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history) { item ->
                HistoryItemCard(item = item)
            }
        }
    }
}

@Composable
fun HistoryItemCard(item: TransactionWithBook) {
    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    val borrowDateStr = dateFormat.format(Date(item.transaction.borrowDate))
    val returnDateStr = item.transaction.returnDate?.let { dateFormat.format(Date(it)) } ?: "-"
    
    val statusColor = if (item.transaction.status == "returned") Color.Green else MaterialTheme.colorScheme.primary
    val statusText = if (item.transaction.status == "returned") "Dikembalikan" else "Dipinjam"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                     Text(item.book.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                     if (item.borrowerName != null) {
                        Text("Peminjam: ${item.borrowerName}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.tertiary)
                     }
                }
                Text(
                    statusText,
                    color = statusColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Tanggal Pinjam: $borrowDateStr")
            if (item.transaction.status == "returned") {
                Text("Tanggal Kembali: $returnDateStr")
                if (item.transaction.fine > 0) {
                     Text("Denda: Rp ${item.transaction.fine.toInt()}", color = MaterialTheme.colorScheme.error)
                }
                if (item.transaction.returnCondition != null) {
                    Text("Kondisi: ${item.transaction.returnCondition}", style = MaterialTheme.typography.bodySmall)
                }
            } else {
                 val dateFormatFull = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                 val dueDateStr = dateFormatFull.format(Date(item.transaction.dueDate))
                 Text("Batas Waktu: $dueDateStr")
            }
        }
    }
}
