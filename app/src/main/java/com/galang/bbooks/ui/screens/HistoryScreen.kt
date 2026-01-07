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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.galang.bbooks.ui.components.LiquidGlassCard
import com.galang.bbooks.ui.components.StatusBadge
import com.galang.bbooks.ui.theme.*
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
    var selectedFilter by remember { mutableStateOf("Semua") }
    val filters = listOf("Semua", "Dipinjam", "Dikembalikan")

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
                .background(BlueAccent.copy(alpha = 0.06f))
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 200.dp, y = 400.dp)
                .clip(CircleShape)
                .background(PurpleAccent.copy(alpha = 0.05f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Header
            Text(
                text = "Riwayat Aktivitas",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                filters.forEach { filter ->
                    FilterTab(
                        text = filter,
                        isSelected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Filter history
            val filteredHistory = when (selectedFilter) {
                "Dipinjam" -> history.filter { it.transaction.status == "borrowed" }
                "Dikembalikan" -> history.filter { it.transaction.status == "returned" }
                else -> history
            }

            if (filteredHistory.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(GlassWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Belum Ada Riwayat",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Aktivitas peminjaman dan pengembalian\nbuku Anda akan muncul di sini.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextTertiary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(filteredHistory) { item ->
                        HistoryItemCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterTab(
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
private fun HistoryItemCard(item: TransactionWithBook) {
    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    val borrowDateStr = dateFormat.format(Date(item.transaction.borrowDate))
    val returnDateStr = item.transaction.returnDate?.let { dateFormat.format(Date(it)) } ?: "-"
    
    val isReturned = item.transaction.status == "returned"

    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Book Cover Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = if (isReturned) {
                                listOf(StatusGreen.copy(alpha = 0.3f), StatusGreen.copy(alpha = 0.1f))
                            } else {
                                listOf(PurpleAccent.copy(alpha = 0.3f), BlueAccent.copy(alpha = 0.1f))
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = if (isReturned) StatusGreen else PurpleAccent,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.book.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        if (item.borrowerName != null) {
                            Text(
                                text = item.borrowerName,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextTertiary
                            )
                        }
                    }
                    StatusBadge(
                        text = if (isReturned) "Dikembalikan" else "Dipinjam",
                        color = if (isReturned) StatusGreen else PurpleAccent
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dates
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Pinjam:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                        Text(
                            text = borrowDateStr,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }
                    Column {
                        Text(
                            text = "Kembali:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                        Text(
                            text = returnDateStr,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }
                }

                // Fine and Condition
                if (isReturned) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (item.transaction.fine > 0) {
                            StatusBadge(
                                text = "Denda: Rp ${item.transaction.fine.toInt()}",
                                color = StatusRed
                            )
                        }
                        if (item.transaction.returnCondition != null) {
                            StatusBadge(
                                text = item.transaction.returnCondition,
                                color = StatusOrange
                            )
                        }
                    }
                } else {
                    // Show due date
                    val dateFormatFull = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val dueDateStr = dateFormatFull.format(Date(item.transaction.dueDate))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Batas: $dueDateStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            }
        }
    }
}
